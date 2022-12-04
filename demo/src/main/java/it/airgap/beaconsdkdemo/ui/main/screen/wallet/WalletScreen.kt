package it.airgap.beaconsdkdemo.ui.main.screen.wallet

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import it.airgap.beaconsdkdemo.ui.main.screen.wallet.state.WalletAction
import it.airgap.beaconsdkdemo.ui.main.screen.wallet.state.WalletViewState

@Composable
fun WalletScreen(
    state: WalletViewState,
    onAction: (WalletAction) -> Unit,
) {
    Text(text = "Wallet")
}

@Preview
@Composable
fun InitStatePreview() {
    WalletScreen(state = WalletViewState(), onAction = {})
}