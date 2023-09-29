package com.chatter.omeglechat.presentation.homescreeen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserElement(
    user: User,
    userClickCallback: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
    ) {
        UserBubble(
            user = user,
            userClickCallback = userClickCallback
        )
        Spacer(modifier = Modifier.padding(horizontal = 5.dp))
        Column {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = user.name)
                // TODO: Implement a referential date property of some kind to each message.
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = user.chatLog.last().content,
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        color = if (user.chatLog.last().seen) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                    )
                )
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = user.chatLog.count { it.seen }.toString(),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}