
import it.airgap.beaconsdk.blockchain.tezos.data.TezosNetwork
import it.airgap.beaconsdk.blockchain.tezos.data.TezosPermission
import it.airgap.beaconsdk.blockchain.tezos.message.request.PermissionTezosRequest
import it.airgap.beaconsdk.blockchain.tezos.message.response.PermissionTezosResponse
import it.airgap.beaconsdk.core.data.AppMetadata
import it.airgap.beaconsdk.core.data.Origin
import it.airgap.beaconsdk.core.data.Threshold
import it.airgap.beaconsdk.core.internal.blockchain.MockBlockchain
import it.airgap.beaconsdk.core.internal.utils.failWith
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

// -- extensions --

internal fun JsonObject.Companion.fromValues(values: Map<String, Any?>, includeNulls: Boolean = false): JsonObject {
    val content = (if (includeNulls) values else values.filterValues { it != null })
        .mapValues {
            when (val value = it.value) {
                is String -> JsonPrimitive(value)
                is Number -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                is JsonElement -> value
                null -> JsonNull
                else -> failWith("Can't create JsonObject from $value, unknown type")
            }
        }

    return JsonObject(content)
}

// -- factories --

internal fun permissionTezosRequest(
    id: String = "id",
    senderId: String = "senderId",
    appMetadata: AppMetadata = AppMetadata(senderId, "mockApp"),
    network: TezosNetwork = TezosNetwork.Custom(),
    scopes: List<TezosPermission.Scope> = emptyList(),
    blockchainIdentifier: String = MockBlockchain.IDENTIFIER,
    origin: Origin = Origin.P2P(senderId),
    version: String = "version",
): PermissionTezosRequest = PermissionTezosRequest(id, version, blockchainIdentifier, senderId, appMetadata, origin, network, scopes)

internal fun permissionTezosResponse(
    id: String = "id",
    publicKey: String = "publicKey",
    blockchainIdentifier: String = MockBlockchain.IDENTIFIER,
    network: TezosNetwork = TezosNetwork.Custom(),
    scopes: List<TezosPermission.Scope> = emptyList(),
    threshold: Threshold? = null,
    version: String = "version",
    requestOrigin: Origin = Origin.P2P("senderId"),
): PermissionTezosResponse = PermissionTezosResponse(id, version, requestOrigin, blockchainIdentifier, publicKey, network, scopes, threshold)