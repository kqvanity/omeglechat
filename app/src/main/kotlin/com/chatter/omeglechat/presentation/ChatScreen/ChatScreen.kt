package com.chatter.omeglechat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.ChatScreen.BottomBar
import com.chatter.omeglechat.ChatScreen.ChatViewModel
import com.chatter.omeglechat.ChatScreen.MainContent
import com.chatter.omeglechat.ChatScreen.TopChattingBar
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import com.chatter.omeglechat.presentation.ChatScreen.ChatViewModelMock
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    arrowBackCallback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopChattingBar(
                scrollBehavior = scrollBehavior,
                connectionState = chatViewModel.connectionState,
                commonInterests = chatViewModel.commonInterests,
                searchButtonCallback = {
                    // TODO: I guess I'll implement another dropdown menu or something, entailing things like block ,save chatlog, etc
                },
                arrowBackCallback = arrowBackCallback
            )
        },
        bottomBar = {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                BottomBar(
                    textMessageState = "",
//                    textMessageState = chatViewModel.textMessage.value,
                    onSendClick = {
                        //  I guess this button should have some other implementation for the onClick callback
                        // for example if the TextField content is empty, there the Send button is deactivated in some way
                        chatViewModel.sendTextMessage()
                        coroutineScope.launch {
                            // Scroll to the bottom
                            // todo: I guess it needs a bit of refinement. (Still not fully working)
                            // Like what if the user explicitly scrolled up. Should i force him down with each new message, or maybe i should notify him of a new incoming message like whatsapp does?
                            scrollState.layoutInfo.totalItemsCount.let {
                                if (it > 0) {
                                    scrollState.animateScrollToItem(it - 1)
                                }
                            }
                        }
                    },
                    onTerminateClick = {
                        chatViewModel.terminate()
                    },
                    onValueChange = {
                        chatViewModel.textMessage.value = it
                    },
                    // TODO: It's a relic of the past (before refactoring business logic code to the chatViewModel). Refine it later on.
//                enabled = ConnectionStates.values().toMutableList().map { it.displayName }.contains(chatViewModel.connectionState),
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 5.dp
                        )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        content = { paddingValue ->
            MainContent(
                paddingValue = paddingValue,
                messages = chatViewModel.messages.toMutableList(),
                scrollState = scrollState,
                modifier = Modifier
                    .padding(10.dp)
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    )
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatScreen() {
    OmegleChatTheme {
        ChatScreen(
            chatViewModel = ChatViewModelMock(),
            arrowBackCallback = {}
        )
    }
}