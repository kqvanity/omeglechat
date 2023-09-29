package com.chatter.omeglechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setSoftInputMode(        // Stop the keyboard pushing the top app bar off the screen.
//            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
//        )
//        val darkThemeState = chatViewModel.darkThemeState.value ?: false
        setContent {
            OmegleChatTheme(
                darkTheme = false
            ) {
                val coroutineScope = rememberCoroutineScope()
                val navController = rememberNavController()
                val navigationDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navBackStackEntry = navController.currentBackStackEntry?.destination?.route
//                                    val currentScreenId = rememberSaveable { mutableStateOf("") }
//                                    currentScreenId.value = it.id
                ModalNavigationDrawer(
                    drawerContent = {
                        WholeDrawer(
                            onItemCallback = {
                                navController.navigate(it.screen.route) {
                                    navController.graph.startDestinationRoute?.let {route ->
                                        popUpTo(route) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                coroutineScope.launch {
                                    navigationDrawerState.close()
                                }
                            }
                        )
                    },
                    gesturesEnabled = true,
                    drawerState = navigationDrawerState,
                    content = {
                        Scaffold(
                            topBar = {
                            },
                            content = { padding ->
                                setupNavGraph(
                                    navController = navController,
                                    padding = padding
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                )
            }
        }
    }
}