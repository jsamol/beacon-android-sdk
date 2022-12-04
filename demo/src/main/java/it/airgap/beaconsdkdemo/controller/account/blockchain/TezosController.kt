package it.airgap.beaconsdkdemo.controller.account.blockchain

import it.airgap.beaconsdkdemo.data.Account
import it.airgap.beaconsdkdemo.data.DerivationPath
import it.airgap.beaconsdkdemo.data.Mnemonic
import it.airgap.beaconsdkdemo.data.PrivateAccount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TezosController @Inject constructor() : BlockchainController<PrivateAccount.Tezos> {
    override val supportedAccountType: Account.Type = Account.Tezos

    override fun createAccount(derivationPath: DerivationPath?): PrivateAccount.Tezos {
        TODO()
    }

    override fun importAccount(mnemonic: Mnemonic, password: String?, derivationPath: DerivationPath?): PrivateAccount.Tezos {
        TODO()
    }
}