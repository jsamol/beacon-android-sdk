package it.airgap.beaconsdkdemo.ui.main.screen.wallet.state

import it.airgap.beaconsdkdemo.data.Account
import it.airgap.beaconsdkdemo.data.DerivationPath
import it.airgap.beaconsdkdemo.data.Mnemonic
import it.airgap.beaconsdkdemo.ui.base.screen.state.Action
import it.airgap.beaconsdkdemo.ui.base.screen.state.ActionResult

sealed interface WalletAction : Action {
    object LoadAccounts : WalletAction
    data class CreateAccount(
        val type: Account.Type,
        val derivationPath: DerivationPath? = null,
    ) : WalletAction

    data class ImportAccount(
        val type: Account.Type,
        val mnemonic: Mnemonic,
        val password: String? = null,
        val derivationPath: DerivationPath? = null,
    ) : WalletAction
}

sealed interface WalletActionResult : ActionResult {
    data class LoadedAccounts(val accounts: List<Account>) : WalletActionResult
    data class NewAccount(val account: Account) : WalletActionResult

    object Loading : WalletActionResult
    data class Error(val throwable: Throwable) : WalletActionResult
}