package it.airgap.beaconsdkdemo.ui.main.screen.dapp.state

import dagger.hilt.android.scopes.ViewModelScoped
import it.airgap.beaconsdkdemo.ui.base.screen.state.Store
import javax.inject.Inject

@ViewModelScoped
class DAppStore @Inject constructor(dispatcher: DAppDispatcher, reducer: DAppReducer)
    : Store<DAppViewState, DAppAction, DAppActionResult, DAppDispatcher, DAppReducer>(DAppViewState(), dispatcher, reducer)