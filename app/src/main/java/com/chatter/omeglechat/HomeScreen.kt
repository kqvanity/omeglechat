package com.chatter.omeglechat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chatter.omeglechat.ui.theme.OmegleChatTheme
import kotlinx.coroutines.launch


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
                    text = "Hello",
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController?
) {
//    OmegleChatTheme {
        val navigationDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerContent = {
                WholeDrawer(navController = navController)
            },
            gesturesEnabled = navigationDrawerState.isOpen,
            drawerState = navigationDrawerState,
            content = {
                Scaffold(
                    topBar = {
                        AppBar(
                            onNavigationiconClick = {
                                scope.launch {
                                    navigationDrawerState.open()
                                }
                            }
                        )
                    },
                    content = { padding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(padding)
                        ) {
                            Greetings(conversations = userConversations.keys.toList())
                        }
                    }
                )
            }
        )
//    }
}

@Preview(
    showBackground = true,
    widthDp = 320
)
//@Preview( showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode", )
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = null)
}

