package com.morozione.psychologyhelper.domain.entity

data class JournalEntry(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val timestamp: Long
)
