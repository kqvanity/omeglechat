package com.chatter.omeglechat

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.onBoardingScreen.route
    ) {
        composable(route = Screen.onBoardingScreen.route) {
            OnboardingScreen(
                onContinueClicked = { /*TODO*/ },
                navController = navController
            )
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.chatScreen.route) {
            ChatScreen(navController = navController)
        }
        composable(route = Screen.drawerScreen.route) {
            WholeDrawer(navController = navController)
        }
        composable(route = Screen.settingsScreen.route) {
            SettingsScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    object onBoardingScreen : Screen("onboarding_screen")
    object HomeScreen : Screen("home_screen")
    object chatScreen : Screen("chat_screen")
    object drawerScreen : Screen("drawer_screen")
    object settingsScreen : Screen("settings_screen")
}