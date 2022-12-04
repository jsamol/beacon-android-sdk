package it.airgap.beaconsdkdemo.data

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class DerivationPath(val value: String) {
}