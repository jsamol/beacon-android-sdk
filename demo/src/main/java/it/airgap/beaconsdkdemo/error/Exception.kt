package it.airgap.beaconsdkdemo.error

import it.airgap.beaconsdkdemo.data.Account

sealed class BeaconDemoException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

// -- unsupported --

@Throws(UnsupportedBlockchainException::class)
fun failWithUnsupportedBlockchain(blockchain: String): Nothing = throw UnsupportedBlockchainException(blockchain)
class UnsupportedBlockchainException(blockchain: String) : BeaconDemoException(
    message = "Blockchain $blockchain is not supported yet.",
)

@Throws(UnsupportedAccountType::class)
fun failWithUnsupportedAccountType(type: Account.Type): Nothing = throw UnsupportedAccountType(type)
class UnsupportedAccountType(type: Account.Type) : BeaconDemoException(
    message = "Account type $type is not supported yet."
)