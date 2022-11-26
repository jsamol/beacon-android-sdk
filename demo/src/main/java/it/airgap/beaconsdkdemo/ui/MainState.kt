package it.airgap.beaconsdkdemo.ui

import it.airgap.beaconsdkdemo.ui.screen.dapp.DAppState
import it.airgap.beaconsdkdemo.ui.screen.wallet.WalletState

data class MainState(val walletState: WalletState, val dAppState: DAppState)
