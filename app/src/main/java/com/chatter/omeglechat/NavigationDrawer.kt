package com.chatter.omeglechat

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_miniscule))
    ) {
        Text(
            text = "Header",
            fontSize = 60.sp
        )
    }
}


@Composable
fun DrawerBody(
    items: List<MenuItem>,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onitemCallback: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        onitemCallback(item)
                    }
                    )
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun WholeDrawer(
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    val currentScreenId = rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        DrawerHeader()
        DrawerBody(
            items = listOf(
                MenuItem(
                    id = "Home",
                    title = "Home",
                    icon = Icons.Default.Home
                ),
                MenuItem(
                    id = "Chat",
                    title = "Chat",
                    icon = Icons.Default.Chat
                ),
                MenuItem(
                    id = "Settings",
                    title = "Settings",
                    icon = Icons.Default.Settings
                ),
                MenuItem(
                    id = "Help",
                    title = "Help",
                    icon = Icons.Default.Info
                )
            ),
            onitemCallback = {
                navController.run {
                    navController!!
                    currentScreenId.value = it.id
                    when(currentScreenId.value) {
                        "Home" -> navController.navigate(Screen.HomeScreen.route)
                        "Chat" -> navController.navigate(Screen.chatScreen.route)
                        "Settings" -> navController.navigate(Screen.settingsScreen.route)
                    }
                }
            }
        )
    }
}

data class MenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun previewNavigationDrawer() {
    Surface {
        WholeDrawer(navController = null)
    }
}