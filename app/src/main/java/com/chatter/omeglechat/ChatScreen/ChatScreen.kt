package com.chatter.omeglechat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.chatter.omeglechat.ChatScreen.BottomBar
import com.chatter.omeglechat.ChatScreen.MainContent
import com.chatter.omeglechat.ChatScreen.TopBar
import com.chatter.omeglechat.ChatScreen.chatMessages
import com.chatter.omeglechat.ChatScreen.chatViewModel
import com.chatter.omeglechat.ChatScreen.scrollToBottom
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import com.chatter.omeglechat.ui.theme.Typography

import com.polendina.coreLib.ConnectionObserver
import com.polendina.coreLib.NewConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class Message(
    val id: Int,
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatScreen() {
    OmegleChatTheme {
        ChatScreen(navController = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController?,
    modifier: Modifier = Modifier,
) {

    val newConnection by chatViewModel.newConnection.observeAsState(NewConnection())
    val commonInterests by chatViewModel.commonInterests.observeAsState(mutableListOf<String>())
    val connectionState by chatViewModel.connectionState.observeAsState(String())
    val messages by chatViewModel.messages.observeAsState(initial = mutableStateListOf<Message>())
    val scrollState by chatViewModel.scrollState.observeAsState(rememberLazyListState())
    val darkThemeState by chatViewModel.darkThemeState.observeAsState(false)
    val coroutineScope by chatViewModel.coroutineScope.observeAsState(rememberCoroutineScope())

    val currentLocalContext = LocalContext.current
    val interests = PrefDataKeyValueStore(context = currentLocalContext).watchFlag().collectAsState( initial = "" )

    newConnection.setObserver(
        object : ConnectionObserver {
            override fun onConnected(usersCommonInterests: List<String>) {
                chatViewModel.updateConnectionState("Connected")
                chatViewModel.clearMessages()
                chatViewModel.addAllCommonInterests(commonInterests)
            }

            override fun onRecaptchaRequired() { chatViewModel.updateConnectionState("Recaptcha required!") }
            override fun onTyping() { chatViewModel.updateConnectionState("User Typing") }
            override fun onStoppedTyping() { chatViewModel.updateConnectionState("Stale")  }

            override fun onUserDisconnected() {
                chatViewModel.updateConnectionState("Disconnected")
                if (messages.size < 10) {       // Longer conversations should be preserved, in the case of leftover exchange information.
                    messages.clear()
                    chatViewModel.commonInterests.value?.clear()
                    newConnection.initalizeConnection()
                }
            }

            override fun onWaiting() { chatViewModel.updateConnectionState("Waiting") }

            override fun onGotMessage(message: String) {
                chatViewModel.updateConnectionState("Message")
                messages.add(Message(1, message))
                scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
            }

            override fun onError() { println("Error occurred") }
            override fun onEvent(response: String) { println("Event!") }
        }
    )

    OmegleChatTheme(
        darkTheme = darkThemeState
    ) {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopBar(
                    scrollBehavior = scrollBehavior,
                    connectionState = connectionState,
                    navController = navController,
                    commonInterests = commonInterests
                )
            },
            content = { paddingValue ->
                MainContent(
                    paddingValue = paddingValue,
                    messages = messages,
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
                        messages.add(Message(0, textMessageState))
                        chatViewModel.updateConnectionState("Message")
                        textMessageState = ""
                        scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
                    },
                    onTerminateClick = {
                        messages.clear()
                        commonInterests.clear()
                        chatViewModel.updateConnectionState("Disconnected")
                        // I guess this line should be relocated to somewhere else. Maybe when first loading the chat screen?
                        newConnection.setCommonInterests(interests.value.split(",").map { it.trim() }.toMutableList())
                        newConnection.disconnect()
                        if (newConnection.getCCValue().isEmpty()) {
                            newConnection.start()
                        }
                        newConnection.initalizeConnection()
                        Toast.makeText(currentLocalContext, interests.value, Toast.LENGTH_SHORT).show()
                    },
                    onValueChange = {
                        textMessageState = it
//                        if (textMessageState.isEmpty()) {
//                        }
                    },
                    enabled = listOf("Connected", "User Typing", "Message", "Stale").contains(connectionState)
                )
            }
        )
    }
}


// nap
//val list = (0..50).map { it.toString() }
//rememberCoroutineScope()
