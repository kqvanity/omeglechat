package com.chatter.omeglechat.ChatScreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.chatter.omeglechat.domain.model.Message

interface ChatViewModel {
    var messages: SnapshotStateList<Message>
    var connectionState: String
    var commonInterests: SnapshotStateList<String>
    var textMessage: MutableState<String>
    fun sendTextMessage()
    fun terminate()
}