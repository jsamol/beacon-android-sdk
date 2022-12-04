package it.airgap.beaconsdkdemo.ui.base.screen.state

interface Reducer<VS : ViewState, AR : ActionResult> {
    fun reduce(viewState: VS, actionResult: AR): VS
}