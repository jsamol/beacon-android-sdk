package it.airgap.beaconsdkdemo.ui.main.screen.wallet.state

import dagger.Reusable
import it.airgap.beaconsdkdemo.controller.account.AccountController
import it.airgap.beaconsdkdemo.data.Account
import it.airgap.beaconsdkdemo.data.DerivationPath
import it.airgap.beaconsdkdemo.data.Mnemonic
import it.airgap.beaconsdkdemo.error.UnsupportedAccountType
import it.airgap.beaconsdkdemo.ui.base.screen.state.Dispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@Reusable
class WalletDispatcher @Inject constructor(
    private val accountController: AccountController,
) : Dispatcher<WalletAction, WalletActionResult> {
    override fun dispatch(action: WalletAction): Flow<WalletActionResult> = with(action) {
        when (this) {
            WalletAction.LoadAccounts -> loadAccounts()
            is WalletAction.CreateAccount -> createAccount(type, derivationPath)
            is WalletAction.ImportAccount -> importAccount(type, mnemonic, password, derivationPath)
        }.catch { emit(WalletActionResult.Error(it)) }
    }

    private fun loadAccounts(): Flow<WalletActionResult> = flow {
        emit(WalletActionResult.Loading)
        emit(WalletActionResult.LoadedAccounts(accountController.loadAccounts()))
    }

    @Throws(UnsupportedAccountType::class)
    private fun createAccount(type: Account.Type, derivationPath: DerivationPath?): Flow<WalletActionResult> = flow {
        emit(WalletActionResult.Loading)
        emit(WalletActionResult.NewAccount(accountController.createAccount(type, derivationPath)))
    }

    @Throws(UnsupportedAccountType::class)
    private fun importAccount(type: Account.Type, mnemonic: Mnemonic, password: String?, derivationPath: DerivationPath?): Flow<WalletActionResult> = flow {
        emit(WalletActionResult.Loading)
        emit(WalletActionResult.NewAccount(accountController.importAccount(type, mnemonic, password, derivationPath)))
    }
}