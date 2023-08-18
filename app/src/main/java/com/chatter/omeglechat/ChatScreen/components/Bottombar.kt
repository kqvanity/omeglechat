package com.chatter.omeglechat.ChatScreen

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.ui.theme.OmegleChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomBar(
    onSendClick: (textMessageState: String) -> Unit,
    onTerminateClick: () -> Unit,
    onValueChange: (newValue: String) -> Unit,
    textMessageState: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextField(
            enabled = enabled,
            value = textMessageState,
            placeholder = {
                Text(
                    text = "Say Hello!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = TextStyle(
                        color = Color.Gray
                    )
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                IconButton(
                    onClick = onTerminateClick,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSendClick(textMessageState)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = null
                    )
                }
            },
            onValueChange = onValueChange ,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .height(70.dp)
                .clip(CircleShape)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, heightDp = 150)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, heightDp = 300)
@Composable
fun PreviewChatBox() {
    OmegleChatTheme() {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            BottomBar(
                onSendClick = {},
                onTerminateClick = { },
                onValueChange = { },
//                textMessageState = "Content in here",
                textMessageState = "",
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 5.dp
                    )
            )
        }
    }
}