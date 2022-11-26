package it.airgap.beaconsdkdemo.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import it.airgap.beaconsdkdemo.R

sealed interface BottomNavItem {
    @get:StringRes val title: Int
    @get:DrawableRes val icon: Int

    val screen: @Composable () -> Unit
    val screenId: String

    data class Wallet(override val screen: @Composable () -> Unit) : BottomNavItem {
        override val title: Int = R.string.main_bottom_nav_item_title_wallet
        override val icon: Int = R.drawable.ic_wallet_24

        override val screenId: String = SCREEN_ID

        companion object {
            private const val SCREEN_ID = "wallet"
        }
    }

    data class DApp(override val screen: @Composable () -> Unit) : BottomNavItem {
        override val title: Int = R.string.main_bottom_nav_item_title_dapp
        override val icon: Int = R.drawable.ic_dapp_24

        override val screenId: String = SCREEN_ID

        companion object {
            private const val SCREEN_ID = "dapp"
        }
    }
}

@Composable()
fun BottomNavigationGraph(
    navController: NavHostController,
    navItems: List<BottomNavItem>,
    modifier: Modifier,
) {
    NavHost(navController = navController, startDestination = navItems.startDestination, modifier = modifier) {
        navItems.forEach { navItem->
            composable(navItem.screenId) {
                navItem.screen()
            }
        }
    }

}

@Composable
fun BottomNavigation(
    navController: NavController,
    navItems: List<BottomNavItem>,
) {
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        navItems.forEach { navItem ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = navItem.icon), contentDescription = stringResource(id = navItem.title)) },
                label = { Text(text = stringResource(id = navItem.title)) },
                selected = currentDestination?.hierarchy?.any { it.route == navItem.screenId } == true,
                onClick = {
                    navController.navigate(navItem.screenId) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}

private val List<BottomNavItem>.startDestination: String
    get() = first().screenId