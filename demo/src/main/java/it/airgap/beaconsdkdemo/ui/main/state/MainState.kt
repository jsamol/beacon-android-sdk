package it.airgap.beaconsdkdemo.ui.main.state

import it.airgap.beaconsdkdemo.ui.main.screen.dapp.state.DAppViewState
import it.airgap.beaconsdkdemo.ui.main.screen.wallet.state.WalletViewState

data class MainState(val walletState: WalletViewState, val dAppState: DAppViewState)
