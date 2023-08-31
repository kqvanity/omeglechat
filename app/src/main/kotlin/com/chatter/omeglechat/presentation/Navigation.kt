package com.chatter.omeglechat

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chatter.omeglechat.videoscreen.components.VideoScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.onBoardingScreen.route
    ) {
        composable(route = Screen.onBoardingScreen.route) {
            OnboardingScreen(
                onContinueClicked = {
                    navController.navigate(Screen.HomeScreen.route)
                }
            )
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.videoScreen.route) {
            VideoScreen(navController = navController)
        }
        composable(route = Screen.chatScreen.route) {
            ChatScreen(
                arrowBackCallback = {
                    navController.navigate(Screen.HomeScreen.route)
                }
            )
        }
        composable(route = Screen.drawerScreen.route) {
            WholeDrawer(
                navController = navController,
                onItemCallback = {
                    navController.run {
//                        val currentScreenId = rememberSaveable { mutableStateOf("") }
//                        currentScreenId.value = it.id
                        when(it.id) {
                            "home" -> navController.navigate(Screen.HomeScreen.route)
                            "chat" -> navController.navigate(Screen.chatScreen.route)
                            "video" -> navController.navigate(Screen.videoScreen.route)
                            "settings" -> navController.navigate(Screen.settingsScreen.route)
                        }
                    }
                }
            )
        }
        composable(route = Screen.settingsScreen.route) {
            SettingsScreen(
                navController = navController,
            )
        }
    }
}

sealed class Screen(val route: String) {
    object onBoardingScreen : Screen("onboarding_screen")
    object HomeScreen : Screen("home_screen")
    object chatScreen : Screen("chat_screen")
    object videoScreen : Screen("video_screen")
    object drawerScreen : Screen("drawer_screen")
    object settingsScreen : Screen("settings_screen")
}