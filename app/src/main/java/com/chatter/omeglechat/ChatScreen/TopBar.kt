package com.chatter.omeglechat.ChatScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.chatter.omeglechat.HomeScreen
import com.chatter.omeglechat.Screen
import com.chatter.omeglechat.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    connectionState: String,
    navController: NavController?,
    commonInterests: MutableList<String>,
    modifier: Modifier = Modifier,
) {
    val showHomeScreen = remember { mutableStateOf(false) }
    if (showHomeScreen.value) HomeScreen(null)

    TopAppBar(
        title = {
            Column(
            ) {
                Text(
                    text = connectionState,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (commonInterests.size > 0 && commonInterests[0].isNotEmpty()) "You both like ${commonInterests.joinToString(", ") }!" else "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.bodySmall,
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
//                    showHomeScreen.value = true
                    navController?.navigate(Screen.HomeScreen.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
//                    if (buttonIconState == Icons.Default.Send) {
//                    } else if (buttonIconState == Icons.Default.Close) {
//                    }
                }
            ) {
                /*
                    - Add
                        - This button should serve as a way to add another user
                            - It'd only if he's using the same app.
                 */
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(MaterialTheme.colorScheme.primary),
        scrollBehavior = scrollBehavior,
        modifier = Modifier
    )
}