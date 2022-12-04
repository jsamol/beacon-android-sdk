package it.airgap.beaconsdkdemo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.airgap.beaconsdkdemo.app.BeaconDemoConfiguration
import it.airgap.beaconsdkdemo.ui.main.screen.dapp.state.DAppAction
import it.airgap.beaconsdkdemo.ui.main.screen.dapp.state.DAppStore
import it.airgap.beaconsdkdemo.ui.main.screen.wallet.state.WalletAction
import it.airgap.beaconsdkdemo.ui.main.screen.wallet.state.WalletStore
import it.airgap.beaconsdkdemo.ui.main.state.MainState
import it.airgap.beaconsdkdemo.utils.combineState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appConfiguration: BeaconDemoConfiguration,
    private val walletStore: WalletStore,
    private val dAppStore: DAppStore,
) : ViewModel() {
    val state: StateFlow<MainState> = combineState(walletStore.viewStateFlow, dAppStore.viewStateFlow) { walletState, dAppState ->
        MainState(walletState, dAppState)
    }

    fun onWalletAction(action: WalletAction) {
        viewModelScope.launch {
            walletStore.intent(action)
        }
    }

    fun onDAppAction(action: DAppAction) {
        viewModelScope.launch {
            dAppStore.intent(action)
        }
    }
}