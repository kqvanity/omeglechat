package com.chatter.omeglechat.domain.model
data class UserPreferences(
    var enableNotifications: Boolean,
    var darkMode: Boolean,
    var languageMatch: Boolean,
    var language: String,
    var userInterests: String,
    var autoReply: Boolean,
    var autoSkip: Boolean,
    var age: Int
)