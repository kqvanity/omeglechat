package com.chatter.omeglechat

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
import androidx.compose.runtime.toMutableStateList
import com.chatter.omeglechat.preferences.PreferencesViewModel

import com.polendina.lib.ConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class Message(
    val id: Int,
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    val currentLocalContext = LocalContext.current
    val scrollState = rememberLazyListState()
//    val coroutineScope by chatViewModel.coroutineScope.observeAsState(rememberCoroutineScope())
    val coroutineScope = rememberCoroutineScope()
    // General user interests (saved in the settings DataStore, and the matching interests returned by Omegle's server)
//    val userInterests by PreferencesDataStore(context = currentLocalContext).getUserInterests().collectAsState(initial = listOf())
    val userInterests = preferencesViewModel.user
    val prohibitedIds = remember { mutableStateListOf<String>() }

    chatViewModel.newConnection.setObserver(object : ConnectionObserver {

        override fun onConnected(usersCommonInterests: List<String>) {
            chatViewModel.connectionState = ConnectionStates.CONNECTED.state
            chatViewModel.commonInterests = usersCommonInterests.toMutableStateList()

            if (prohibitedIds.contains(chatViewModel.newConnection.getClientId())) {
                Log.d("BLOCK", "Blocked ${chatViewModel.newConnection.getClientId()}")
                chatViewModel.messages.clear()
                chatViewModel.connectionState = ConnectionStates.DISCONNECTED.state
                chatViewModel.commonInterests.clear()
                // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
                coroutineScope.launch(Dispatchers.IO) {
                    chatViewModel.newConnection.disconnect()
                    chatViewModel.newConnection.setCommonInterests(userInterests.toMutableList())
                    chatViewModel.newConnection.start()
                }
            }
        }

        override fun onRecaptchaRequired() { chatViewModel.connectionState = ConnectionStates.RECAPTCHA_REQUIRED.state }
        override fun onTyping() { chatViewModel.connectionState = ConnectionStates.USER_TYPING.state }
        override fun onStoppedTyping() { chatViewModel.connectionState = ConnectionStates.STALE.state }

        override fun onUserDisconnected() {
            chatViewModel.connectionState = ConnectionStates.DISCONNECTED.state
            if (chatViewModel.messages.size < 10) {       // Longer conversations should be preserved, in the case of leftover exchange information.
                chatViewModel.messages.clear()
                chatViewModel.commonInterests.clear()
                chatViewModel.newConnection.setCommonInterests(userInterests.toMutableList())
                CoroutineScope(Dispatchers.IO).launch {
                    chatViewModel.newConnection.start()
                }
            }
        }

        override fun onWaiting() { chatViewModel.connectionState = ConnectionStates.WAITING.state }

        override fun onGotMessage(message: String) {
            chatViewModel.connectionState = ConnectionStates.MESSAGE.state
            chatViewModel.messages.add(Message(1, message))
            scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
        }

        override fun onError() { println("Error occurred") }
        override fun onEvent(response: String) { println("Event!") }

    })

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopChattingBar(
                scrollBehavior = scrollBehavior,
                connectionState = chatViewModel.connectionState,
                commonInterests = chatViewModel.commonInterests,
                searchButtonCallback = {
                    prohibitedIds.add(chatViewModel.newConnection.getClientId())
                    Log.d("BLOCK", chatViewModel.newConnection.getClientId())
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
                messages = chatViewModel.messages.toMutableList(),
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
                    coroutineScope.launch(Dispatchers.IO) {
                        chatViewModel.newConnection.sendText(textMessageState)
                    }
                    chatViewModel.messages.add(Message(0, textMessageState))
                    chatViewModel.connectionState = ConnectionStates.MESSAGE.state
                    textMessageState = ""
                    scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
                },
                onTerminateClick = {
                    chatViewModel.messages.clear()
                    chatViewModel.connectionState = ConnectionStates.DISCONNECTED.state
                    chatViewModel.commonInterests.clear()
                    // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
                    coroutineScope.launch(Dispatchers.IO) {
                        chatViewModel.newConnection.disconnect()  // This should be refined or something. I can't just sent a disconnect request at every single brand opening of the chatting screen
                        chatViewModel.newConnection.setCommonInterests(userInterests.toMutableList())
                        chatViewModel.newConnection.start()
                    }
                },
                onValueChange = {
                    textMessageState = it
//                        if (textMessageState.isEmpty()) {
//                        }
                },
                enabled = ConnectionStates.values().toMutableList().map { it.state }.contains(chatViewModel.connectionState),
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

