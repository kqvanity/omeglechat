package com.chatter.omeglechat.domain.model

import java.util.Date

data class Message(
    val id: Int,
    val text: String,
    val date: Date
)