package com.morozione.psychologyhelper.domain.entity

data class MoodEntry(
    val id: String,
    val userId: String,
    val mood: Mood,
    val note: String,
    val timestamp: Long
)
