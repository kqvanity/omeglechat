package com.chatter.omeglechat.ChatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(
                horizontal = 5.dp
            )
    ) {
        OutlinedTextField(
            enabled = enabled,
            value = textMessageState,
            placeholder = {
                Text( text = "Hello!" )
            },
//            colors = TextFieldColors(
//            ),
            leadingIcon = {
                IconButton(
                    onClick = onTerminateClick
                ) {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSendClick(textMessageState)
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = null)
                }
            },
            onValueChange = onValueChange ,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatBox() {
    BottomBar(
        onSendClick = {},
        onTerminateClick = { },
        onValueChange = { },
        textMessageState = "",
        enabled = true
    )
}