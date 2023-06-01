package com.chatter.omeglechat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

import com.polendina.coreLib.ConnectionObserver
import com.polendina.coreLib.NewConnection

data class Message(
    val id: Int,
    val message: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatScreen() {
    ChatScreen(navController = null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController?,
    modifier: Modifier = Modifier,
) {
    val connectionState = remember { mutableStateOf("") }

    val messages = remember { mutableStateListOf<Message>() }

    /*
        - The current implementation is just a placeholder before actually implementing it properly with a toggle button and whatnot
    */
    val darkThemeState by remember { mutableStateOf(false) }

    val newConnection = MainConnectionSetup(connectionState = connectionState, messages = messages)
    newConnection.setMutualTopics(UserPreferences().getMutualTopics())
    newConnection.start()

//        val list = (0..50).map { it.toString() }

    OmegleChatTheme(
        darkTheme = darkThemeState
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            modifier = Modifier
//                .background(Color.White)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopBar(
                    scrollBehavior = scrollBehavior,
                    newConnection = newConnection,
                    messages = messages,
                    connectionState = connectionState,
                    navController = navController
                )
            },
            content = { paddingValue ->
                MainContent(
                    paddingValue = paddingValue,
                    messages = messages
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
//                        darkThemeState = !darkThemeState
                        newConnection.disconnect()
                        newConnection.start()
                        messages.clear()
                    }
                ) {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                }
            },
//        floatingActionButtonPosition = FabPosition.End,
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { /*TODO*/ }
//            ) {
//            }
//        },
            bottomBar = {
                BottomBar(newConnection = newConnection, messages = messages)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    newConnection: NewConnection,
    messages: SnapshotStateList<Message>,
    connectionState: MutableState<String>,
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    val showHomeScreen = remember { mutableStateOf(false) }
    if (showHomeScreen.value) HomeScreen(null)

    TopAppBar(
        title = {
            Text(
                text = connectionState.value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    paddingValue: PaddingValues,
    messages: SnapshotStateList<Message>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
//        contentPadding = PaddingValues(20.dp),
        contentPadding = paddingValue,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth()
    ) {
        items(
            messages
        ) { message ->
            /*
                - Setting different 'horizontalArrangement', and 'color' conditionally, depending on the type of the message.
             */
            Row(
                horizontalArrangement = if (message.id == 0) Arrangement.End else Arrangement.Start,
                modifier = Modifier
                    .padding(
                        vertical = 10.dp,
                        horizontal = 20.dp
                    )
                    .fillMaxWidth()
            ) {
                Card(
//                    onClick = { /*TODO*/ },
                    colors = CardDefaults.cardColors(containerColor = if (message.id == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer) ,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Text(
                        // text = "Message ${messages.value[it]}",
                        text = message.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier
                            .padding(10.dp)
                            .wrapContentWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomBar(
    newConnection: NewConnection,
    messages: SnapshotStateList<Message>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(
                horizontal = 5.dp
            )
    ) {
//        val textState by remember { mutableStateOf(TextFieldValue()) }
        var textState by remember { mutableStateOf("") }
        val buttonIconState by remember { mutableStateOf(Icons.Default.Send) }
        OutlinedTextField(
            value = textState,
//            colors = TextFieldColors(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        /*
                            - I guess this button should have some other implementation for the onClick callback
                                - for example if the TextField content is empty, there the Send button is deactivated in some way
                         */
//                        Toast.makeText("Hello", textState.value.text, Toast.LENGTH_LONG).show()
                        newConnection.sendText(textState)
                        Log.i("Message", textState) // Debug
                        messages.add(Message(0, textState))
                        textState = ""
                    }
                ) {
                   Icon(imageVector = Icons.Filled.Send, contentDescription = null)
                }
            },
            onValueChange = {
                textState = it
//                if (textState.isEmpty()) {
//                }
            },
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        )
//        Button(
//            onClick = {
//            },
//            modifier = Modifier
//                .weight(2f)
//                .clip(CircleShape)
//        ) {
//            Icon(
//                imageVector = buttonIconState,
//                contentDescription = null
//            )
//        }
    }
//            BottomAppBar(
//                modifier = Modifier
//                    .background(Color.White)
//            ) {
//            }
}


@Composable
fun MainConnectionSetup(
    connectionState: MutableState<String>,
    messages: SnapshotStateList<Message>
): NewConnection {
    val newConnection = NewConnection(object : ConnectionObserver {
        override fun onEvent(response: String) {
            println("Event!")
        }

        override fun onConnected() {
            connectionState.value = "Connected"
        }

        override fun onRecaptchaRequired() {
            connectionState.value = "Recaptcha required!"
        }

        override fun onTyping() {
            connectionState.value = "User Typing"
        }

        override fun onStoppedTyping() {
            connectionState.value = ""
        }

        override fun onUserDisconnected() {
            connectionState.value = "User disconnected"
            messages.clear()
        }

        override fun onWaiting() {
            connectionState.value = "Waiting"
        }

        override fun onGotMessage(message: String) {
            connectionState.value = ""
            messages.add(Message(1, message))
        }

        override fun onSomethingElse() {
            println("Something else!")
        }

    })
    return (newConnection)
}

