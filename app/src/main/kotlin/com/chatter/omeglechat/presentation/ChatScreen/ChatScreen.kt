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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chatter.omeglechat.ChatScreen.BottomBar
import com.chatter.omeglechat.ChatScreen.ChatViewModel
import com.chatter.omeglechat.ChatScreen.MainContent
import com.chatter.omeglechat.ChatScreen.TopChattingBar
import com.chatter.omeglechat.ChatScreen.scrollToBottom
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel(),
    arrowBackCallback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLocalContext = LocalContext.current
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
//                    prohibitedIds.add(chatViewModel.newConnection.getClientId())
//                    Log.d("BLOCK", chatViewModel.newConnection.getClientId())
                },
                arrowBackCallback = arrowBackCallback
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
        bottomBar = {
            BottomBar(
                 textMessageState = chatViewModel.textMessage.value,
                 onSendClick = {
                    //  I guess this button should have some other implementation for the onClick callback
                    // for example if the TextField content is empty, there the Send button is deactivated in some way
                    chatViewModel.sendTextMessage()
                    scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
                },
                onTerminateClick = {
                    chatViewModel.terminate()
                },
                onValueChange = {
                    chatViewModel.textMessage.value = it
                },
                // TODO: It's a relic of the past (before refactoring business logic code to the chatViewModel). Refine it later on.
//                enabled = ConnectionStates.values().toMutableList().map { it.state }.contains(chatViewModel.connectionState),
                enabled = true,
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatScreen() {
    OmegleChatTheme {
        ChatScreen(
            arrowBackCallback = {}
        )
    }
}

