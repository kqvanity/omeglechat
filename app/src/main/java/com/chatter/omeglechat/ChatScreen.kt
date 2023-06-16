package com.chatter.omeglechat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import com.chatter.omeglechat.ui.theme.Typography

import com.polendina.coreLib.ConnectionObserver
import com.polendina.coreLib.NewConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.internal.checkDuration

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
    val connectionState = remember { mutableStateOf("") }

//    val messages = remember { mutableStateListOf<Message>(*messagess.toTypedArray()) }  // Converting a list to a vararg argument. nap
    val messages = remember { mutableStateListOf<Message>() }

    /*
        - The current implementation is just a placeholder before actually implementing it properly with a toggle button and whatnot
    */
    val darkThemeState by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()       // nap

    val scrollState = rememberLazyListState()

    val currentLocalContext = LocalContext.current

    /*
        todo
            - This portion is contained within a composable function, that gets called arbitrarily.
                - Mind the fact that I guess some conversation I had were halted, because of this problem
     */
    val newConnection = MainConnectionSetup(
        connectionState = connectionState,
        coroutineScope = coroutineScope,
        scrollState = scrollState,
        messages = messages
    )

    val interests = PrefDataKeyValueStore(context = currentLocalContext).watchFlag().collectAsState( initial = "" )

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
                    messages = messages,
                    scrollState = scrollState
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
//                        darkThemeState = !darkThemeState
                        coroutineScope.launch {
                            newConnection.setMutualTopics(interests.value.split(",").map { it.trim() }.toMutableList())
                            newConnection.start()
                            Toast.makeText(currentLocalContext, interests.value, Toast.LENGTH_SHORT).show()
                        }
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
                BottomBar(
                    newConnection = newConnection,
                    messages = messages,
                    coroutineScope = coroutineScope,
                    scrollState = scrollState
                )
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
    scrollState: LazyListState,         // nap
    modifier: Modifier = Modifier
) {
    LazyColumn(
//        contentPadding = PaddingValues(20.dp),
        contentPadding = paddingValue,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        state = scrollState,    // nap
//        reverseLayout = true,           // nap
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
                    onClick = { /*TODO*/ },
                    colors = CardDefaults.cardColors(containerColor = if (message.id == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    val localCurrentContext = LocalContext.current
                    var showMore by remember { mutableStateOf(false) }
                    var maxLines by remember { mutableStateOf(10) }
                    var moreLessButton by remember { mutableStateOf("more") }
                    val annotatedString = buildAnnotatedString {
                        val handleStyle = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        val handlePattern = "@(\\w+)".toRegex()
                        message.text.split(regex = "\\s+".toRegex()).forEach {
                            if (it.matches(handlePattern)) {
                                pushStringAnnotation( tag = "handle", annotation = it )
                                withStyle(style = handleStyle) {
                                    append(text = it)
                                }
                                pop()       // Don't forget to end 'pushStringAnnotation' with 'pop()'

//                                    appendInlineContent(
//                                        id = "handle",
//                                    )
                            } else {
                                append(it)
                            }
                            append(" ")
                        }
                    }
                    ClickableText(
                        /*
                            - nap
                                - Annotated string
                                    - It allows to style parts of our text or paragraph.
                                    - the withStyle method to apply the style to the portion of the text we need to style.
                         */
                        // text = "Message ${messages.value[it]}",
                        text = annotatedString,
//                        inlineContent = {
//                            "hello" to InlineTextContent(
//                                placeholder = Placeholder(
//                                    width
//                                )
//                            ) {
//                                TextButton(onClick = { /*TODO*/ }) {
//
//                                }
//                            }
//                        },
                        style = Typography.bodyMedium,
                        /*
                            - nap
                                - If text doesn't fix in the available space, we show an ellipsis and a button to view the entire text.
                                - onTextLayout
                                    - It returns text layer results with measurement information captured after the layout phase.
                                    - You don't emit UI inside onTextLayout lambda, because this call is not part of the composition.
                                        - Instead, you can change a state variable triggering a recomposition, and then decide to show a button or not based on the value of the state variable.
                         */
                        // Mitigate spammy messages
                        maxLines = maxLines,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = {
                            if (it.hasVisualOverflow) {
                                showMore = true
                            }
                        },
                        onClick = { offset ->
                            // The sole reason I'm having annotatedString as a separate variable, is because of its usage here (there's a better compact approach? whoknows!)
                            annotatedString.getStringAnnotations(tag = "handle", start = offset, end = offset).firstOrNull()?.let {
                                Toast.makeText(localCurrentContext, "${it.item} copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .padding(10.dp)
                            .wrapContentWidth()
                        )
                    if (showMore) {
                        TextButton(
                            onClick = {
                                maxLines = if (maxLines == 10) Int.MAX_VALUE else 10    // Not sure if this is the optimal way to go about it.
                                moreLessButton = if (moreLessButton == "more") "less" else "more"
                            },
                            content = {
                                Text(text = moreLessButton)
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                        )
                    }
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
    scrollState: LazyListState,
    coroutineScope: CoroutineScope,
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
                        scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
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
    scrollState: LazyListState,
    coroutineScope: CoroutineScope,
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
            scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
            messages.clear()
        }

        override fun onWaiting() {
            connectionState.value = "Waiting"
        }

        override fun onGotMessage(message: String) {
            connectionState.value = ""
            messages.add(Message(1, message))
            scrollToBottom(scrollState = scrollState, coroutineScope = coroutineScope)
        }

        override fun onSomethingElse() {
            println("Something else!")
        }

    })
    return (newConnection)
}

/**
 * Scroll to the bottom of a lazy list. It can be conveniently used when in conversation with each new message in the current user view.
 *
 * @param scrollState The state of the lazy list, upon which this method will work on.
 * @param coroutineScope the coroutine scope that's used to run the animateScrollToItem method.
 */
private fun scrollToBottom(scrollState: LazyListState, coroutineScope: CoroutineScope) { // nap
    /*
        - I guess it needs a bit of refinement.
            - Like what if the user explicitly scrolled up.
            - Should i force him down with each new message, or maybe i should notify him of a new incoming message like whatsapp does?
     */
    coroutineScope.launch {     // nap
        val lazyColumnItemsCount = scrollState.layoutInfo.totalItemsCount   // nap
        if (lazyColumnItemsCount > 0) {
            scrollState.animateScrollToItem(lazyColumnItemsCount - 1)     // Scroll to the last item. Still not working fully
        }
    }
}

