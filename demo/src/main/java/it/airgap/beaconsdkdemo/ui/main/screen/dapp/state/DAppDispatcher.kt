package it.airgap.beaconsdkdemo.ui.main.screen.dapp.state

import dagger.Reusable
import it.airgap.beaconsdkdemo.ui.base.screen.state.Dispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Reusable
class DAppDispatcher @Inject constructor() : Dispatcher<DAppAction, DAppActionResult> {
    override fun dispatch(action: DAppAction): Flow<DAppActionResult> {
        TODO("Not yet implemented")
    }
}