package it.airgap.beaconsdkdemo.ui.base.screen.state

import kotlinx.coroutines.flow.Flow

interface Dispatcher<A : Action, AR : ActionResult> {
    fun dispatch(action: A): Flow<AR>
}