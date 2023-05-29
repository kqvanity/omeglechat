package com.chatter.omeglechat

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.ui.theme.OmegleChatTheme


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
    val expandedState = remember { mutableStateOf(false) }
    val extraPadding = animateDpAsState(
        targetValue = if (expandedState.value) 40.dp else 0.dp,
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
                        bottom = extraPadding.value.coerceAtLeast(0.dp)
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
                if (expandedState.value) {
                    Text(
                        text = ("Composem ipsum color sit lazy, " + "padding theme elit. sed do bouncy. ").repeat(2)
                    )
                }
            }
            OutlinedButton(
                onClick = {
                    expandedState.value = !expandedState.value
                },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Text(
                    text = if (!expandedState.value) stringResource(id = R.string.show_more) else stringResource(id = R.string.show_less),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320
)
//@Preview( showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode", )
@Composable
fun HomeScreen(
) {
    OmegleChatTheme() {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Greetings(conversations = userConversations.keys.toList())
        }
    }
}

