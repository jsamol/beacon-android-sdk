package it.airgap.beaconsdkdemo.controller.account.blockchain

import dagger.Reusable
import it.airgap.beaconsdkdemo.data.Account
import it.airgap.beaconsdkdemo.data.DerivationPath
import it.airgap.beaconsdkdemo.data.Mnemonic
import it.airgap.beaconsdkdemo.data.PrivateAccount
import it.airgap.beaconsdkdemo.error.failWithUnsupportedBlockchain
import javax.inject.Inject

@Reusable
class SubstrateController @Inject constructor() : BlockchainController<PrivateAccount.Substrate> {
    override val supportedAccountType: Account.Type = Account.Substrate

    override fun createAccount(derivationPath: DerivationPath?): PrivateAccount.Substrate {
        failWithUnsupported()
    }

    override fun importAccount(mnemonic: Mnemonic, password: String?, derivationPath: DerivationPath?): PrivateAccount.Substrate {
        failWithUnsupported()
    }

    private fun failWithUnsupported(): Nothing =
        failWithUnsupportedBlockchain(NAME)

    companion object {
        private const val NAME = "Substrate"
    }
}