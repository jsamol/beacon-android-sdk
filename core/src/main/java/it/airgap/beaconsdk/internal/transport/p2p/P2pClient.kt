package it.airgap.beaconsdk.internal.transport.p2p

import it.airgap.beaconsdk.internal.crypto.Crypto
import it.airgap.beaconsdk.internal.crypto.data.KeyPair
import it.airgap.beaconsdk.internal.crypto.data.SessionKeyPair
import it.airgap.beaconsdk.internal.transport.p2p.data.P2pMessage
import it.airgap.beaconsdk.internal.transport.p2p.matrix.MatrixClient
import it.airgap.beaconsdk.internal.transport.p2p.matrix.data.MatrixEvent
import it.airgap.beaconsdk.internal.transport.p2p.matrix.data.MatrixRoom
import it.airgap.beaconsdk.internal.transport.p2p.utils.P2pCommunicationUtils
import it.airgap.beaconsdk.internal.transport.p2p.utils.P2pServerUtils
import it.airgap.beaconsdk.internal.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

internal class P2pClient(
    private val appName: String,
    private val serverUtils: P2pServerUtils,
    private val communicationUtils: P2pCommunicationUtils,
    private val matrixClients: List<MatrixClient>,
    private val replicationCount: Int,
    private val crypto: Crypto,
    private val keyPair: KeyPair,
) {
    private val subscribedFlows: MutableMap<HexString, MutableSet<String>> = mutableMapOf()

    private val serverSessionKeyPair: MutableMap<HexString, SessionKeyPair> = mutableMapOf()
    private val clientSessionKeyPair: MutableMap<HexString, SessionKeyPair> = mutableMapOf()

    private val matrixEvents: Flow<MatrixEvent> by lazy {
        matrixClients
            .map(MatrixClient::events)
            .merge()
            .onStart { matrixClients.filterNot { it.isLoggedIn() }.launch { it.start() } }
    }

    private val matrixMessageEvents: Flow<MatrixEvent.TextMessage> get() = matrixEvents.filterIsInstance()
    private val matrixInviteEvents: Flow<MatrixEvent.Invite> get() = matrixEvents.filterIsInstance()

    fun isSubscribed(publicKey: HexString): Boolean = subscribedFlows.containsKey(publicKey)

    fun subscribeTo(publicKey: HexString): Flow<InternalResult<P2pMessage>> {
        val identifier = UUID.randomUUID().toString().also { subscribedFlows.addTo(publicKey, it) }

        return flow {
            try {
                matrixMessageEvents
                    .filter { it.isTextMessageFrom(publicKey) }
                    .map { decrypt(publicKey, it) }
                    .map { P2pMessage.fromInternalResult(publicKey, it) }
                    .collect {
                        if (subscribedFlows.containsElement(publicKey, identifier)) emit(it)
                        else cancelFlow()
                    }
            } catch (e: CancellationException) {
                /* no action */
            }
        }
    }

    fun unsubscribeFrom(publicKey: HexString) {
        subscribedFlows.remove(publicKey)
    }

    suspend fun sendTo(publicKey: HexString, message: String): InternalResult<Unit> =
        tryResult {
            val encrypted = encrypt(publicKey, message).value().asHexString()
            for (i in 0 until replicationCount) {
                val relayServer = serverUtils.getRelayServer(publicKey, i.asHexString())
                val recipient = communicationUtils.recipientIdentifier(publicKey, relayServer)

                matrixClients.launch { it.sendTextMessageTo(recipient, encrypted.value()).value() }
            }
        }

    suspend fun sendPairingRequest(
        publicKey: HexString,
        relayServer: String,
        version: String?,
    ): InternalResult<Unit> =
        tryResult {
            val recipient = communicationUtils.recipientIdentifier(publicKey, relayServer)
            val payload = crypto.encryptMessageWithPublicKey(
                communicationUtils.pairingPayload(
                    version,
                    appName,
                    keyPair.publicKey,
                    serverUtils.getRelayServer(keyPair.publicKey)
                ),
                publicKey.asByteArray()
            ).value().asHexString()
            val message = communicationUtils.channelOpeningMessage(recipient, payload.value())

            matrixClients.launch { it.sendTextMessageTo(recipient, message).value() }
        }

    private fun encrypt(publicKey: HexString, message: String): InternalResult<ByteArray> =
        getOrCreateClientSessionKeyPair(publicKey)
            .flatMap { crypto.encryptMessageWithSharedKey(message, it.tx) }

    private fun decrypt(
        publicKey: HexString,
        textMessage: MatrixEvent.TextMessage,
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
        clientSessionKeyPair.getOrCreate(publicKey) {
            crypto.createClientSessionKeyPair(publicKey.asByteArray(), keyPair.privateKey)
        }

    private fun getOrCreateServerSessionKeyPair(publicKey: HexString): InternalResult<SessionKeyPair> =
        serverSessionKeyPair.getOrCreate(publicKey) {
            crypto.createServerSessionKeyPair(publicKey.asByteArray(), keyPair.privateKey)
        }

    private fun MutableMap<HexString, SessionKeyPair>.getOrCreate(
        publicKey: HexString,
        default: () -> InternalResult<SessionKeyPair>,
    ): InternalResult<SessionKeyPair> =
        if (containsKey(publicKey)) Success(getValue(publicKey))
        else default().alsoIfSuccess { put(publicKey, it) }

    private fun P2pMessage.Companion.fromInternalResult(
        publicKey: HexString,
        message: InternalResult<String>,
    ): InternalResult<P2pMessage> =
        message.map { P2pMessage(publicKey.value(), it) }

    private suspend fun MatrixClient.start() {
        if (isLoggedIn()) return

        tryLog(TAG) {
            val loginDigest = crypto.hash(
                "login:${currentTimestamp() / 1000 / (5 * 60)}",
                32
            ).value()

            val signature = crypto.signMessageDetached(loginDigest, keyPair.privateKey)
                .value()
                .asHexString()
                .value()

            val publicKeyHex = keyPair.publicKey
                .asHexString()
                .value()

            val id = crypto.hashKey(keyPair.publicKey)
                .value()
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

    private suspend fun MatrixClient.sendTextMessageTo(
        recipient: String,
        message: String,
    ): InternalResult<Unit> =
        tryResult {
            start()

            val room = getRelevantRoom(recipient).value()
            room?.let { sendTextMessage(it, message).value() }
        }

    private suspend fun MatrixClient.getRelevantRoom(recipient: String): InternalResult<MatrixRoom?> {
        start()

        return joinedRooms()
            .firstOrNull { it.members.contains(recipient) }
            ?.let {
                logDebug(TAG, "Channel already open, reusing ${it.id}")
                Success(it)
            } ?: run {
            logDebug(TAG, "No relevant rooms found")
            createTrustedPrivateRoom(recipient)
        }
    }

    private fun <K, V> MutableMap<K, MutableSet<V>>.addTo(key: K, value: V) {
        getOrPut(key) { mutableSetOf() }.add(value)
    }

    private fun <K, V> MutableMap<K, MutableSet<V>>.containsElement(key: K, value: V): Boolean =
        get(key)?.contains(value) ?: false

    private fun cancelFlow(): Nothing = throw CancellationException()

    companion object {
        const val TAG = "P2pClient"
    }
}