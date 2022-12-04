package it.airgap.beaconsdkdemo.ui.main.screen.wallet.state

import dagger.Reusable
import it.airgap.beaconsdkdemo.ui.base.screen.state.Reducer
import javax.inject.Inject

@Reusable
class WalletReducer @Inject constructor() : Reducer<WalletViewState, WalletActionResult> {
    override fun reduce(viewState: WalletViewState, actionResult: WalletActionResult): WalletViewState {
        TODO("Not yet implemented")
    }
}