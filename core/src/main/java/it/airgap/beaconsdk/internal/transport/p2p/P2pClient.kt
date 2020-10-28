package it.airgap.beaconsdk.internal.transport.p2p

import it.airgap.beaconsdk.internal.BeaconConfig
import it.airgap.beaconsdk.internal.crypto.Crypto
import it.airgap.beaconsdk.internal.crypto.data.KeyPair
import it.airgap.beaconsdk.internal.crypto.data.SessionKeyPair
import it.airgap.beaconsdk.internal.transport.p2p.data.P2pHandshakeInfo
import it.airgap.beaconsdk.internal.transport.p2p.data.P2pMessage
import it.airgap.beaconsdk.internal.transport.p2p.matrix.MatrixClient
import it.airgap.beaconsdk.internal.transport.p2p.matrix.data.client.MatrixEvent
import it.airgap.beaconsdk.internal.transport.p2p.matrix.data.client.MatrixRoom
import it.airgap.beaconsdk.internal.transport.p2p.utils.P2pCommunicationUtils
import it.airgap.beaconsdk.internal.transport.p2p.utils.P2pServerUtils
import it.airgap.beaconsdk.internal.utils.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//@ExperimentalCoroutinesApi
internal class P2pClient(
    private val appName: String,
    private val serverUtils: P2pServerUtils,
    private val communicationUtils: P2pCommunicationUtils,
    private val matrixClients: List<MatrixClient>,
    private val replicationCount: Int,
    private val crypto: Crypto,
    private val keyPair: KeyPair
) {
    private val messageFlows: MutableMap<HexString, Flow<InternalResult<String>>> = mutableMapOf()

    private val serverSessionKeyPair: MutableMap<HexString, SessionKeyPair> = mutableMapOf()
    private val clientSessionKeyPair: MutableMap<HexString, SessionKeyPair> = mutableMapOf()

    private val matrixEvents: Flow<MatrixEvent> by lazy {
        matrixClients
            .map(MatrixClient::events)
            .merge()
            .onStart { matrixClients.filterNot { it.isLoggedIn() }.launch { it.start() } }
    }

    private val matrixMessageEvents: Flow<MatrixEvent.TextMessage> by lazy {
        matrixEvents.filterIsInstance()
    }
    private val matrixInviteEvents: Flow<MatrixEvent.Invite> by lazy {
        matrixEvents.filterIsInstance()
    }

    private val handshakeInfo: P2pHandshakeInfo
        get() = P2pHandshakeInfo(
            appName,
            BeaconConfig.versionName,
            keyPair.publicKey.asHexString().value(),
            serverUtils.getRelayServer(keyPair.publicKey)
        )

    private val handshakeInfoJson: String
        get() = Json.encodeToString(handshakeInfo)

    fun subscribeTo(publicKey: HexString): Flow<InternalResult<P2pMessage>> =
        messageFlows.getOrPut(publicKey) {
            matrixMessageEvents
                .filter { it.isTextMessageFrom(publicKey) }
                .map { decrypt(publicKey, it) }
        }.map { P2pMessage.fromInternalResult(publicKey, it) }

    suspend fun sendTo(publicKey: HexString, message: String): InternalResult<Unit> =
        tryResult {
            val encrypted = encrypt(publicKey, message).getOrThrow().asHexString()
            for (i in 0 until replicationCount) {
                val relayServer = serverUtils.getRelayServer(publicKey, i.asHexString())
                val recipient = communicationUtils.getRecipientIdentifier(publicKey, relayServer)

                matrixClients.launch { it.sendTextMessageTo(recipient, encrypted.value()).getOrThrow() }
            }
        }

    suspend fun sendPairingRequest(
        publicKey: HexString,
        relayServer: String
    ): InternalResult<Unit> =
        tryResult {
            val recipient = communicationUtils.getRecipientIdentifier(publicKey, relayServer)
            val payload = crypto.encryptMessageWithPublicKey(
                keyPair.publicKey.asHexString().value(), // master compatibility, for feat/v2 encrypt handshakeInfoJson
                publicKey.asByteArray()
            ).getOrThrow().asHexString()
            val message = communicationUtils.createOpenChannelMessage(recipient, payload.value())

            matrixClients.launch { it.sendTextMessageTo(recipient, message).getOrThrow() }
        }

    private fun encrypt(publicKey: HexString, message: String): InternalResult<ByteArray> =
        getOrCreateClientSessionKeyPair(publicKey)
            .flatMap { crypto.encryptMessageWithSharedKey(message, it.tx) }

    private fun decrypt(
        publicKey: HexString,
        textMessage: MatrixEvent.TextMessage
    ): InternalResult<String> =
        getOrCreateServerSessionKeyPair(publicKey)
            .flatMap {
                if (textMessage.message.isHex()) crypto.decryptMessageWithSharedKey(HexString.fromString(textMessage.message), it.rx)
                else crypto.decryptMessageWithSharedKey(textMessage.message, it.rx)
            }
            .map { it.decodeToString() }

    private fun MatrixEvent.isTextMessageFrom(publicKey: HexString): Boolean =
        this is MatrixEvent.TextMessage
                && communicationUtils.isMessageFrom(this, publicKey)
                && crypto.validateEncryptedMessage(message)

    private fun getOrCreateClientSessionKeyPair(publicKey: HexString): InternalResult<SessionKeyPair> =
        clientSessionKeyPair.getOrCreate(publicKey) { crypto.createClientSessionKeyPair(publicKey.asByteArray(), keyPair.privateKey) }

    private fun getOrCreateServerSessionKeyPair(publicKey: HexString): InternalResult<SessionKeyPair> =
        serverSessionKeyPair.getOrCreate(publicKey) { crypto.createServerSessionKeyPair(publicKey.asByteArray(), keyPair.privateKey) }

    private fun MutableMap<HexString, SessionKeyPair>.getOrCreate(
        publicKey: HexString,
        default: () -> InternalResult<SessionKeyPair>
    ): InternalResult<SessionKeyPair> =
        if (containsKey(publicKey)) internalSuccess(getValue(publicKey))
        else default().alsoIfSuccess { put(publicKey, it) }

    private fun P2pMessage.Companion.fromInternalResult(
        publicKey: HexString,
        message: InternalResult<String>
    ): InternalResult<P2pMessage> =
        message.map { P2pMessage(publicKey.value(), it) }

    private suspend fun MatrixClient.start() {
        if (isLoggedIn()) return

        tryLog(TAG) {
            val loginDigest = crypto.hash(
                "login:${currentTimestamp() / 1000 / (5 * 60)}",
                32
            ).getOrThrow()

            val signature = crypto.signMessageDetached(loginDigest, keyPair.privateKey)
                .getOrThrow()
                .asHexString()
                .value()

            val publicKeyHex = keyPair.publicKey
                .asHexString()
                .value()

            val id = crypto.hashKey(keyPair.publicKey)
                .getOrThrow()
                .asHexString()
                .value()

            val password = "ed:$signature:${publicKeyHex}"
            val deviceId = publicKeyHex

            start(id, password, deviceId)

            CoroutineScope(CoroutineName("collectInviteEvents") + Dispatchers.Default).launch {
                matrixInviteEvents.collect {
                    joinRooms(it.roomId)
                }
            }
        }
    }

    private suspend fun MatrixClient.sendTextMessageTo(recipient: String, message: String): InternalResult<Unit> =
        tryResult {
            start()

            val room = getRelevantRoom(recipient).getOrThrow()
            room?.let { sendTextMessage(it, message).getOrThrow() }
        }

    private suspend fun MatrixClient.getRelevantRoom(recipient: String): InternalResult<MatrixRoom?> {
        start()

        return joinedRooms()
            .firstOrNull { it.members.contains(recipient) }
            ?.let {
                logDebug(TAG, "Channel already open, reusing ${it.id}")
                internalSuccess(it)
            } ?: run {
            logDebug(TAG, "No relevant rooms found")
            createTrustedPrivateRoom(recipient)
        }
    }

    companion object {
        const val TAG = "P2pClient"
    }
}