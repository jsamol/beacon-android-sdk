package it.airgap.beaconsdkdemo.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import it.airgap.beaconsdkdemo.ui.main.navigation.BottomNavItem
import it.airgap.beaconsdkdemo.ui.main.navigation.BottomNavigation
import it.airgap.beaconsdkdemo.ui.main.navigation.BottomNavigationGraph
import it.airgap.beaconsdkdemo.ui.main.screen.dapp.state.DAppAction
import it.airgap.beaconsdkdemo.ui.main.screen.dapp.DAppScreen
import it.airgap.beaconsdkdemo.ui.main.screen.dapp.state.DAppViewState
import it.airgap.beaconsdkdemo.ui.main.screen.wallet.state.WalletAction
import it.airgap.beaconsdkdemo.ui.main.screen.wallet.WalletScreen
import it.airgap.beaconsdkdemo.ui.main.screen.wallet.state.WalletViewState
import it.airgap.beaconsdkdemo.ui.main.state.MainState
import it.airgap.beaconsdkdemo.ui.main.theme.BeaconSDKTheme
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(viewModel.state, viewModel::onWalletAction, viewModel::onDAppAction)
        }
    }
}

@Composable
fun MainContent(
    stateFlow: StateFlow<MainState>,
    onWalletAction: (WalletAction) -> Unit,
    onDAppAction: (DAppAction) -> Unit,
) {
    BeaconSDKTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            val state by stateFlow.collectAsState()
            Content(state, onWalletAction, onDAppAction)
        }
    }
}

@Composable
fun Content(
    state: MainState,
    onWalletAction: (WalletAction) -> Unit,
    onDAppAction: (DAppAction) -> Unit,
) {
    with(state) {
        val navController = rememberNavController()
        val navItems = listOf(
            BottomNavItem.Wallet(screen = { WalletScreen(walletState, onWalletAction) }),
            BottomNavItem.DApp(screen = { DAppScreen(dAppState, onDAppAction) }),
        )

        Scaffold(
            bottomBar = { BottomNavigation(navController = navController, navItems = navItems) }
        ) { innerPadding ->
            BottomNavigationGraph(
                navController = navController,
                navItems = navItems,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BeaconSDKTheme {
        Content(
            MainState(walletState = WalletViewState(), dAppState = DAppViewState()),
            onWalletAction = {},
            onDAppAction = {},
        )
    }
}