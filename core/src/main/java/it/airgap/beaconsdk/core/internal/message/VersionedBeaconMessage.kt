package it.airgap.beaconsdk.core.internal.message

import androidx.annotation.RestrictTo
import it.airgap.beaconsdk.core.data.Origin
import it.airgap.beaconsdk.core.internal.message.v1.V1BeaconMessage
import it.airgap.beaconsdk.core.internal.message.v2.V2BeaconMessage
import it.airgap.beaconsdk.core.internal.storage.StorageManager
import it.airgap.beaconsdk.core.internal.utils.KJsonSerializer
import it.airgap.beaconsdk.core.internal.utils.getString
import it.airgap.beaconsdk.core.message.BeaconMessage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.jsonObject

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Serializable(with = VersionedBeaconMessage.Serializer::class)
public abstract class VersionedBeaconMessage {
    public abstract val type: String
    public abstract val version: String

    public abstract suspend fun toBeaconMessage(origin: Origin, storageManager: StorageManager): BeaconMessage

    public companion object {
        public fun from(senderId: String, content: BeaconMessage): VersionedBeaconMessage {
            return when (content.version.major) {
                "1" -> V1BeaconMessage.from(senderId, content)
                "2" -> V2BeaconMessage.from(senderId, content)

                // fallback to the newest version
                else -> V2BeaconMessage.from(senderId, content)
            }
        }

        private val String.major: String
            get() = substringBefore('.')
    }

    @OptIn(ExperimentalSerializationApi::class)
    internal object Serializer : KJsonSerializer<VersionedBeaconMessage> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VersionedBeaconMessage") {
            element<String>("version")
        }

        override fun deserialize(jsonDecoder: JsonDecoder, jsonElement: JsonElement): VersionedBeaconMessage {
            val version = jsonElement.jsonObject.getString(descriptor.getElementName(0))

            return when (version.major) {
                "1" -> jsonDecoder.json.decodeFromJsonElement(V1BeaconMessage.serializer(), jsonElement)
                "2" -> jsonDecoder.json.decodeFromJsonElement(V2BeaconMessage.serializer(), jsonElement)

                // fallback to the newest version
                else -> jsonDecoder.json.decodeFromJsonElement(V2BeaconMessage.serializer(), jsonElement)
            }
        }

        override fun serialize(jsonEncoder: JsonEncoder, value: VersionedBeaconMessage) {
            when (value) {
                is V1BeaconMessage -> jsonEncoder.encodeSerializableValue(V1BeaconMessage.serializer(), value)
                is V2BeaconMessage -> jsonEncoder.encodeSerializableValue(V2BeaconMessage.serializer(), value)
            }
        }
    }
}