package com.chatter.omeglechat.presentation.ChatScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.domain.model.Message
import com.chatter.omeglechat.presentation.ChatScreen.ChatViewModelMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageAlertDialog(
    message: Message,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /*TODO*/ }) {
        Card (
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .height(350.dp)
                .width(600.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    )
                    .fillMaxHeight()
            ) {
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Text(
                        text = message.text,
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                        ),
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    MessageClickActions.entries.forEachIndexed {index, messageAction ->
                        Row (
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 5.dp
                                )
                        ) {
                            Text(
                                text = messageAction.title,
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Icon(
                                imageVector = messageAction.icon,
                                contentDescription = null
                            )
                        }
                        if (index != MessageClickActions.entries.lastIndex) Divider()
                    }
                }
            }
        }
    }
}

enum class MessageClickActions(
    val title: String,
    val icon: ImageVector,
    val action: () -> Unit
) {
    COPY(title = "Copy", icon = Icons.Outlined.ContentCopy, action = {}),
    REPLY(title = "Reply", icon = Icons.Outlined.Reply, action = {}),
    FORWARD(title = "Forward", icon = Icons.Outlined.ArrowForward, action = {}),
    DELETE(title = "Delete", icon = Icons.Outlined.Delete, action = {})
}

@Preview
@Composable
fun MessageAlertDialogPreview() {
    MessageAlertDialog(
        message = ChatViewModelMock().messages.random()
    )
}