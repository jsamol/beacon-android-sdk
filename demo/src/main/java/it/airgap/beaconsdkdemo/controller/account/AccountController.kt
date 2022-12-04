package it.airgap.beaconsdkdemo.controller.account

import dagger.hilt.android.scopes.ViewModelScoped
import it.airgap.beaconsdkdemo.app.BeaconDemoStorage
import it.airgap.beaconsdkdemo.controller.account.blockchain.BlockchainController
import it.airgap.beaconsdkdemo.data.Account
import it.airgap.beaconsdkdemo.data.DerivationPath
import it.airgap.beaconsdkdemo.data.Mnemonic
import it.airgap.beaconsdkdemo.error.UnsupportedAccountType
import it.airgap.beaconsdkdemo.error.failWithUnsupportedAccountType
import javax.inject.Inject

@ViewModelScoped
class AccountController @Inject constructor(
    private val appStorage: BeaconDemoStorage,
    private val blockchainControllers: List<BlockchainController<*>>
) {
    fun loadAccounts(): List<Account> = appStorage.accounts

    @Throws(UnsupportedAccountType::class)
    fun createAccount(type: Account.Type, derivationPath: DerivationPath?): Account =
        selectController(type).createAccount(derivationPath)
            .also { appStorage.saveAccount(it) }
            .toPublic()

    @Throws(UnsupportedAccountType::class)
    fun importAccount(type: Account.Type, mnemonic: Mnemonic, password: String?, derivationPath: DerivationPath?): Account =
        selectController(type).importAccount(mnemonic, password, derivationPath)
            .also { appStorage.saveAccount(it) }
            .toPublic()

    @Throws(UnsupportedAccountType::class)
    private fun selectController(type: Account.Type): BlockchainController<*> =
        blockchainControllers.find { it.supportedAccountType == type } ?: failWithUnsupportedAccountType(type)
}