package com.chatter.omeglechat.presentation.navigation

sealed class Screen(val route: String) {
    object onBoardingScreen : Screen("onboarding_screen")
    object HomeScreen : Screen("home_screen")
    object chatScreen : Screen("chat_screen")
    object videoScreen : Screen("video_screen")
    object drawerScreen : Screen("drawer_screen")
    object settingsScreen : Screen("settings_screen")
    object helpScreen : Screen("help_screen")
}