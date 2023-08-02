package com.chatter.omeglechat

import android.app.Application
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chatter.omeglechat.ChatScreen.BottomBar
import com.chatter.omeglechat.ChatScreen.ChatViewModel
import com.chatter.omeglechat.ChatScreen.MainContent
import com.chatter.omeglechat.ChatScreen.TopChattingBar
import com.chatter.omeglechat.ChatScreen.scrollToBottom
import com.chatter.omeglechat.preferences.PreferencesDataStore
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateListOf

import com.polendina.lib.ConnectionObserver

data class Message(
    val id: Int,
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatScreen() {
    OmegleChatTheme {
        ChatScreen(
            navController = null,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavController?
) {
    val currentLocalContext = LocalContext.current

    val chatViewModel = viewModel<ChatViewModel>()

    val newConnection by remember {
        chatViewModel.getNewConnection()
    }
    val connectionState by remember {
        chatViewModel.getConnectionState()
    }
    val messages = remember {
        chatViewModel.getMessages()
    }
//    val scrollState = chatViewModel.scrollState
    val scrollState = rememberLazyListState()

//    val coroutineScope by chatViewModel.coroutineScope.observeAsState(rememberCoroutineScope())
    val coroutineScope = rememberCoroutineScope()

    val userPreferences = PreferencesDataStore(context = currentLocalContext)
    // General user interests (saved in the settings DataStore, and the matching interests returned by Omegle's server)
    val userInterests by userPreferences.getUserInterests().collectAsState(initial = listOf())
    val commonInterests = remember {
        chatViewModel.getCommonInterests()
    }

    val prohibitedIds = remember { mutableStateListOf<String>() }

    newConnection.setObserver(
        object : ConnectionObserver {

            override fun onConnected(usersCommonInterests: List<String>) {
                chatViewModel.updateConnectionState(newState = ConnectionStates.CONNECTED.state)
                chatViewModel.updateCommonInterests(commonInterests = usersCommonInterests)

                if (prohibitedIds.contains(newConnection.getClientId())) {
                    Log.d("BLOCK", "Blocked ${newConnection.getClientId()}")
                    chatViewModel.clearMessages()
                    chatViewModel.updateConnectionState(newState = ConnectionStates.DISCONNECTED.state)
                    chatViewModel.updateCommonInterests(commonInterests = listOf())
                    // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
                    newConnection.disconnect()
                    if (newConnection.getCCValue().isEmpty()) {
                        newConnection.start()
                    }
                    newConnection.setCommonInterests(userInterests.toMutableList())
                    newConnection.initalizeConnection()
                }
            }

            override fun onRecaptchaRequired() { chatViewModel.updateConnectionState(newState = ConnectionStates.RECAPTCHA_REQUIRED.state) }
            override fun onTyping() { chatViewModel.updateConnectionState(newState = ConnectionStates.USER_TYPING.state) }
            override fun onStoppedTyping() { chatViewModel.updateConnectionState(newState = ConnectionStates.STALE.state)  }

            override fun onUserDisconnected() {
                chatViewModel.updateConnectionState(newState = ConnectionStates.DISCONNECTED.state)
                if (messages.size < 10) {       // Longer conversations should be preserved, in the case of leftover exchange information.
                    chatViewModel.clearMessages()
                    chatViewModel.updateCommonInterests(mutableListOf<String>())
                    newConnection.setCommonInterests(userInterests.toMutableList())
                    newConnection.initalizeConnection()
                }
            }

            override fun onWaiting() { chatViewModel.updateConnectionState(newState = ConnectionStates.WAITING.state) }

            override fun onGotMessage(message: String) {
                chatViewModel.updateConnectionState(newState = ConnectionStates.MESSAGE.state)
                chatViewModel.addMessage(Message(1, message))
                scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
            }

            override fun onError() { println("Error occurred") }
            override fun onEvent(response: String) { println("Event!") }
        }
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopChattingBar(
                scrollBehavior = scrollBehavior,
                connectionState = connectionState,
                commonInterests = commonInterests,
                searchButtonCallback = {
                    prohibitedIds.add(newConnection.getClientId())
                    Log.d("BLOCK", newConnection.getClientId())
                },
                arrowBackCallback = {
                    navController?.navigate(Screen.HomeScreen.route)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        content = { paddingValue ->
            MainContent(
                paddingValue = paddingValue,
                messages = messages.toMutableList(),
                scrollState = scrollState
            )
        },
//        floatingActionButtonPosition = FabPosition.End,
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { /*TODO*/ }
//            ) {
//            }
//        },
        bottomBar = {
            var textMessageState by remember { mutableStateOf("") }
            BottomBar(
                 textMessageState = textMessageState,
                 onSendClick = {
                    /*
                        - I guess this button should have some other implementation for the onClick callback
                            - for example if the TextField content is empty, there the Send button is deactivated in some way
                    */
                    newConnection.sendText(textMessageState)
                    chatViewModel.addMessage(Message(0, textMessageState))
                    chatViewModel.updateConnectionState(newState = ConnectionStates.MESSAGE.state)
                    textMessageState = ""
                    scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
                },
                onTerminateClick = {
                    chatViewModel.clearMessages()
                    chatViewModel.updateConnectionState(newState = ConnectionStates.DISCONNECTED.state)
                    chatViewModel.updateCommonInterests(listOf())
                    // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
                    newConnection.disconnect()
                    if (newConnection.getCCValue().isEmpty()) {
                        newConnection.start()
                    }
                    newConnection.setCommonInterests(userInterests.toMutableList())
                    newConnection.initalizeConnection()
                },
                onValueChange = {
                    textMessageState = it
//                        if (textMessageState.isEmpty()) {
//                        }
                },
                enabled = ConnectionStates.values().toMutableList().map { it.state }.contains(connectionState),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 5.dp
                    )
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    )

}

private enum class ConnectionStates(val state: String) {
    CONNECTED(state = "Connected"),
    DISCONNECTED(state = "Disconnected"),
    USER_TYPING(state = "User Typing"),
    MESSAGE(state = "Message"),
    WAITING(state = "Waiting"),
    RECAPTCHA_REQUIRED(state = "Recaptcha required!"),
    STALE(state = "Stale");

    override fun toString(): String = this.state
}
