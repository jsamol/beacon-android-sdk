package it.airgap.beaconsdkdemo.ui

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
import it.airgap.beaconsdkdemo.ui.navigation.BottomNavItem
import it.airgap.beaconsdkdemo.ui.navigation.BottomNavigation
import it.airgap.beaconsdkdemo.ui.navigation.BottomNavigationGraph
import it.airgap.beaconsdkdemo.ui.screen.dapp.DAppScreen
import it.airgap.beaconsdkdemo.ui.screen.dapp.DAppState
import it.airgap.beaconsdkdemo.ui.screen.wallet.WalletScreen
import it.airgap.beaconsdkdemo.ui.screen.wallet.WalletState
import it.airgap.beaconsdkdemo.ui.theme.BeaconSDKTheme
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(
                viewModel.state,
            )
        }
    }
}

@Composable
fun MainContent(
    stateFlow: StateFlow<MainState>,
) {
    BeaconSDKTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            val state by stateFlow.collectAsState()
            Content(
                state,
            )
        }
    }
}

@Composable
fun Content(
    state: MainState,
) {
    with(state) {
        val navController = rememberNavController()
        val navItems = listOf(
            BottomNavItem.Wallet(screen = { WalletScreen(walletState) }),
            BottomNavItem.DApp(screen = { DAppScreen(dAppState) }),
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
            MainState(walletState = WalletState(), dAppState = DAppState()),
        )
    }
}