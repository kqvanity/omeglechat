package com.chatter.omeglechat

class UserPreferences {
    private var bannedWords: MutableList<String> = mutableListOf(
        "M"
    )
    private var mutualTopics: MutableList<String> = mutableListOf(
        "talk"
    )

    fun getBannedWords(): MutableList<String> {
        return (this.bannedWords)
    }

    fun getMutualTopics(): MutableList<String> {
        return (this.mutualTopics)
    }

}