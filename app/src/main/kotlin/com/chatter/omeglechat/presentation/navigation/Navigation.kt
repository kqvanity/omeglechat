package com.chatter.omeglechat.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chatter.omeglechat.presentation.ChatScreen.ChatScreen
import com.chatter.omeglechat.HomeScreen
import com.chatter.omeglechat.OnboardingScreen
import com.chatter.omeglechat.SettingsScreen
import com.chatter.omeglechat.presentation.ChatScreen.ChatViewModelImpl
import com.chatter.omeglechat.videoscreen.components.VideoScreen

@Composable
fun setupNavGraph(
    navController: NavHostController,
    padding: PaddingValues
) {
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
            HomeScreen(padding = padding)
        }
        composable(route = Screen.videoScreen.route) {
            VideoScreen(navController = navController)
        }
        composable(route = Screen.chatScreen.route) {
            ChatScreen(
                chatViewModel = viewModel<ChatViewModelImpl>(),
                arrowBackCallback = {
                    navController.navigate(Screen.HomeScreen.route)
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