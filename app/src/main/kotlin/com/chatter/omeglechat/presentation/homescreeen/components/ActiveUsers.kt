package com.chatter.omeglechat.presentation.homescreeen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatter.omeglechat.R
import com.chatter.omeglechat.data.network.Connection
import com.chatter.omeglechat.data.network.ConnectionImpl
import kotlin.random.Random

/**
 * A class representing each user remote user.
 *
 * @param name The name of the user.
 * @param chatLog List of messages sent back and forth with that particular user.
 */
class User(
    name: String,
    chatLog: SnapshotStateList<Message>,
    connection: Connection // TODO: This should be renamed to plain 'Connection' and the original code refactor to reflect a class per user.
) {
    val name = name
    val chatLog = chatLog
    val connection = connection
}

/**
 * A data class representing a message.
 *
 * @param id A integer indicating whether it's a message from the user or the other party signified by 0 or 1 respectively.
 * @param content The actual content of the message
 */
data class Message(
    val id: Int,
    val content: String,
    val seen: Boolean
)

private val messages = listOf("Hi", "Hello", "Hi", "Nice to meet you!", "Hi How's life", "Good how about you")
private val userNames = listOf("Sam Andreas", "Karl Johansen", "Romen Reins", "Jake Wharton", "Sam Atlas", "Robert Lewndoeski", "Adms Klein", "Thomas Rivolt", "Kein Atlantis", "Roy Sam", "Adms Kein")
val users = userNames.map { User(name = it, chatLog = messages.shuffled().map { Message(id = Random.nextInt(2), content = it, seen = Random.nextBoolean()) }.toMutableStateList(), connection = ConnectionImpl()) }

@Composable
fun ActiveUsers(
    users: List<User>,
    viewAllCallback: () -> Unit,
    userClickCallback: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
    ) {
        Row (
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            TextButton(onClick = viewAllCallback) {
                Text(text = stringResource(id = R.string.active))
                Text(text = " (${users.count { it.connection.clientId.isEmpty() }})") // TODO: This should be refactored by including a 'Connected' state at the connection class.
            }
        }
        LazyRow (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(users) {
                UserBubble(
                    user = it,
                    userClickCallback = userClickCallback
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActiveUserPreview(
) {
    Column (
        modifier = Modifier
            .height(400.dp)
            .fillMaxWidth()
    ) {
        ActiveUsers(
            users = users,
            viewAllCallback = {},
            userClickCallback = {}
        )
    }
}