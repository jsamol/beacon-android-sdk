package it.airgap.beaconsdkdemo.ui.main.screen.wallet.state

import dagger.hilt.android.scopes.ViewModelScoped
import it.airgap.beaconsdkdemo.ui.base.screen.state.Store
import javax.inject.Inject

@ViewModelScoped
class WalletStore @Inject constructor(dispatcher: WalletDispatcher, reducer: WalletReducer)
    : Store<WalletViewState, WalletAction, WalletActionResult, WalletDispatcher, WalletReducer>(WalletViewState(), dispatcher, reducer)