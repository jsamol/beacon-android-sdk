package it.airgap.beaconsdkdemo.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airgap.beaconsdkdemo.app.BeaconDemoConfiguration
import it.airgap.beaconsdkdemo.ui.screen.dapp.DAppState
import it.airgap.beaconsdkdemo.ui.screen.wallet.WalletState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appConfiguration: BeaconDemoConfiguration,
) : ViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(
        MainState(
            walletState = WalletState(),
            dAppState = DAppState(),
        )
    )
    val state: StateFlow<MainState> by lazy { _state.asStateFlow() }
}