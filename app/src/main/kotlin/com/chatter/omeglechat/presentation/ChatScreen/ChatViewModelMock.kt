package com.chatter.omeglechat.presentation.ChatScreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.chatter.omeglechat.ChatScreen.ChatViewModel
import com.chatter.omeglechat.domain.model.ConnectionStates
import com.chatter.omeglechat.domain.model.Message
import java.time.Instant
import java.util.Date

class ChatViewModelMock(): ChatViewModel {
    override var commonInterests: SnapshotStateList<String> = mutableStateListOf(
        "youtube",
        "politics",
        "gaming'",
        "talk",
        "World of warcraft",
        "CSGO",
        "others"
    )
        set(value) {}
    override var connectionState: String
        get() = ConnectionStates.entries.random().state
        set(value) {}
    override var messages: SnapshotStateList<Message> = mutableStateListOf(
        // A mockup of or a rather spammy message
        Message(0, "Just as you would say that lorem for @naughtycat saying and other things that makes it absolutely ridiculously Just as you @maria say that lorem for additional @Romio and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculsoyl Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously", Date.from(Instant.now())),
        Message(0, "Hi", Date.from(Instant.now())),
        Message(1, "Hello", Date.from(Instant.now())),
        Message(0, "How Area", Date.from(Instant.now())),
        Message(1, "Doing fine", Date.from(Instant.now())),
        Message(0, "Nice to meet  you", Date.from(Instant.now())),
        Message(0, "I really like you", Date.from(Instant.now())),
        Message(1, "How Area", Date.from(Instant.now())),
        Message(1, "Nice to meet  you", Date.from(Instant.now())),
        Message(0, "Doing fine", Date.from(Instant.now())),
        Message(1, "How @fanthom", Date.from(Instant.now())),
        Message(1, "I really like you @Romeo", Date.from(Instant.now())),
        Message(0, "Nice to meet  you", Date.from(Instant.now())),
        Message(0, "Just as you would say that lorem for additional saying and other things that makes it absolutely ridiculously", Date.from(Instant.now()))
    )
        set(value) {}
    override var textMessage: MutableState<String>
        get() = mutableStateOf(messages.random().text)
        set(value) {}

    override fun sendTextMessage() { }
    override fun terminate() { }
}