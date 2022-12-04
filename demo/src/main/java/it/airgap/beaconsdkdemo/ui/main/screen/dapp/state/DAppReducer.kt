package it.airgap.beaconsdkdemo.ui.main.screen.dapp.state

import dagger.Reusable
import it.airgap.beaconsdkdemo.ui.base.screen.state.Reducer
import javax.inject.Inject

@Reusable
class DAppReducer @Inject constructor() : Reducer<DAppViewState, DAppActionResult> {
    override fun reduce(viewState: DAppViewState, actionResult: DAppActionResult): DAppViewState {
        TODO("Not yet implemented")
    }
}