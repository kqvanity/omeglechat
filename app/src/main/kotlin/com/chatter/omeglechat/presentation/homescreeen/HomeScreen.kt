package com.chatter.omeglechat

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.HorizontalRule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatter.omeglechat.presentation.homescreeen.components.ActiveUsers
import com.chatter.omeglechat.presentation.homescreeen.components.User
import com.chatter.omeglechat.presentation.homescreeen.components.UserBubble
import com.chatter.omeglechat.presentation.homescreeen.components.UserElement
import com.chatter.omeglechat.presentation.homescreeen.components.users
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onNavigationiconClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
//        backgroundColor = MaterialTheme.colorScheme.primary,
//        contentColor = MaterialTheme.colorScheme.onPrimary,
        navigationIcon = {
            IconButton(
                onClick = onNavigationiconClick
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun Greetings(conversations: List<String> = listOf()) {
//    Surface(
//        color = MaterialTheme.colorScheme.background
//    ) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize()
    )
    {
        for (converstaion in conversations) {
            Greeting(word = converstaion)
        }
    }
//    }
}

@Composable
fun Greeting(
    word: String,
    modifier: Modifier = Modifier
) {
    var expandedState by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        targetValue = if (expandedState) 40.dp else 0.dp,
//        animationSpec = tween(durationMillis = 200)
        animationSpec = spring(
            Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    );
    Card(
//        color = MaterialTheme.colorScheme.primary,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
            .clip(RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        // Mitigates bugs related to the padding becoming negative, thus crashing the app
                        bottom = extraPadding.coerceAtLeast(0.dp)
                    )
            ) {
                Text(
                    text = "Sep,21",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "${word}!",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (expandedState) {
                    Text(
                        text = ("Composem ipsum color sit lazy, " + "padding theme elit. sed do bouncy. ").repeat(2)
                    )
                }
            }
            OutlinedButton(
                onClick = {
                    expandedState = !expandedState
                },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Text(
                    text = if (!expandedState) stringResource(id = R.string.show_more) else stringResource(
                        id = R.string.show_less
                    ),
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    padding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(padding)
    ) {
        Greetings(conversations = userConversations.keys.toList())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 320)
//@Preview( showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode", )
@Composable
fun HomeScreenPreview() {
    OmegleChatTheme {
//        HomeScreen(
//            padding = PaddingValues()
//        )
        val query by remember { mutableStateOf("") }
        fun onQueryChange(newQuery: String) {}
        fun onSearchChange(newQuery: String) {}
        Scaffold (
            topBar = {
                Column {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.message),
                            style = TextStyle(
                                fontSize = 30.sp
                            )
                        )
                    }
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        SearchBar(
                            query = query,
                            onQueryChange = { onQueryChange(it) },
                            onSearch = { onSearchChange(it) },
                            active = users.isNotEmpty(),
                            onActiveChange = {},
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.search),
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.outline
                                    ),
                                    overflow = TextOverflow.Visible,
                                    modifier = Modifier
                                        .height(40.dp)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null
                                )
                            },
                            shape = SearchBarDefaults.inputFieldShape,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .weight(1f)
                        ) { }
                        IconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .fillMaxHeight()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dehaze,
                                contentDescription = null,
                            )
                        }
                    }
                    ActiveUsers(
                        users = users,
                        viewAllCallback = {},
                        userClickCallback = {},
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                    )
                }
            },
            modifier = Modifier
                .padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                )
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(it)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.chats))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.HorizontalRule, // TODO: This should be three horizontal lines. Ellipses or something.
                            contentDescription = null
                        )
                    }
                }
                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(users) {
                        UserElement(
                            user = it,
                            userClickCallback = {},
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}