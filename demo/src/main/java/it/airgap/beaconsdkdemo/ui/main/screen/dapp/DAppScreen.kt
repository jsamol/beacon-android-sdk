package it.airgap.beaconsdkdemo.ui.main.screen.dapp

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import it.airgap.beaconsdkdemo.ui.main.screen.dapp.state.DAppAction
import it.airgap.beaconsdkdemo.ui.main.screen.dapp.state.DAppViewState

@Composable
fun DAppScreen(
    state: DAppViewState,
    onAction: (DAppAction) -> Unit,
) {
    Text(text = "DApp")
}

@Preview
@Composable
fun InitStatePreview() {
    DAppScreen(state = DAppViewState(), onAction = {})
}