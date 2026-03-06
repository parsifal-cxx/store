package com.example.store.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.store.ui.favorite.FavoriteScreen
import com.example.store.ui.home.components.StoreBottomBar
import com.example.store.ui.profile.ProfileScreen

/** Корневой экран Home. Дата: 05.03.2026, Автор: Бубнов Никита */
@Composable
fun HomeRootScreen(
    onOpenDetails: (String) -> Unit
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf<HomeTab>(HomeTab.Home) }

    Scaffold(
        bottomBar = {
            StoreBottomBar(
                selected = selectedTab,
                onSelect = { tab ->
                    selectedTab = tab
                    navController.navigate(tab.route) {
                        popUpTo(HomeTab.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBagClick = { }
            )
        }
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = HomeTab.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(HomeTab.Home.route) {
                HomeScreen(
                    onOpenDetails = onOpenDetails
                )
            }
            composable(HomeTab.Favorite.route) {
                FavoriteScreen(
                    onBackToHome = { selectedTab = HomeTab.Home; navController.navigate(HomeTab.Home.route) },
                    onOpenDetails = onOpenDetails
                )
            }
            composable(HomeTab.Orders.route) { OrdersScreen() }
            composable(HomeTab.Profile.route) { ProfileScreen() }
        }
    }
}

/** Вкладки нижнего меню. Дата: 05.03.2026, Автор: Бубнов Никита */
sealed class HomeTab(val route: String) {
    data object Home : HomeTab("tab_home")
    data object Favorite : HomeTab("tab_favorite")
    data object Orders : HomeTab("tab_orders")
    data object Profile : HomeTab("tab_profile")
}