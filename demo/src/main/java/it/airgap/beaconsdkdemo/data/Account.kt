package it.airgap.beaconsdkdemo.data

import it.airgap.tezos.core.type.encoded.PublicKey
import it.airgap.tezos.core.type.encoded.SecretKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Account {
    abstract val type: Type
    abstract val createdAt: Long

    @Serializable
    data class Tezos(val publicKey: PublicKey, override val createdAt: Long) : Account() {
        override val type: Type = Companion

        companion object : Type
    }

    @Serializable
    data class Substrate(override val createdAt: Long) : Account() {
        override val type: Type = Companion

        companion object : Type
    }

    sealed interface Type
}

sealed class PrivateAccount {
    abstract val createdAt: Long

    abstract fun toPublic(): Account

    data class Tezos(
        val secretKey: SecretKey,
        val publicKey: PublicKey,
        val derivationPath: DerivationPath,
        override val createdAt: Long
    ) : PrivateAccount() {
        override fun toPublic(): Account = Account.Tezos(publicKey, createdAt)
    }

    data class Substrate(override val createdAt: Long) : PrivateAccount() {
        override fun toPublic(): Account = Account.Substrate(createdAt)
    }
}
