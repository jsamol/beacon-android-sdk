package it.airgap.beaconsdkdemo.controller.account.blockchain

import it.airgap.beaconsdkdemo.data.Account
import it.airgap.beaconsdkdemo.data.DerivationPath
import it.airgap.beaconsdkdemo.data.Mnemonic
import it.airgap.beaconsdkdemo.data.PrivateAccount

interface BlockchainController<PA : PrivateAccount> {
    val supportedAccountType: Account.Type

    fun createAccount(derivationPath: DerivationPath?): PA
    fun importAccount(mnemonic: Mnemonic, password: String?, derivationPath: DerivationPath?): PA
}