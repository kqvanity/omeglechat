package com.chatter.omeglechat.presentation.navigationdrawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.ui.graphics.vector.ImageVector
import com.chatter.omeglechat.presentation.navigation.Screen

enum class MenuItem(
    val id: String,
    val title: String,
    val screen: Screen,
    val icon: ImageVector
) {
    HOME(id = "home", title = "Home", screen = Screen.HomeScreen, icon = Icons.Default.Home),
    CHAT(id = "chat", title = "Chat", screen = Screen.chatScreen, icon = Icons.Default.Chat),
    VIDEO(id = "video", title = "Video", screen = Screen.videoScreen, icon = Icons.Default.VideoCall),
    SETTINGS(id = "settings", title = "Settings", screen = Screen.settingsScreen, icon = Icons.Default.Settings),
    HELP(id = "help", title = "Help", screen = Screen.helpScreen, icon = Icons.Default.Info)
}