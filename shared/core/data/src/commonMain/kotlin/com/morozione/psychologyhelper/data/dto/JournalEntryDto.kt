package com.morozione.psychologyhelper.data.dto

import com.morozione.psychologyhelper.domain.entity.JournalEntry
import kotlinx.serialization.Serializable

@Serializable
data class JournalEntryDto(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = 0L
) {
    fun toDomain(): JournalEntry = JournalEntry(
        id = id,
        userId = userId,
        title = title,
        content = content,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(entry: JournalEntry): JournalEntryDto = JournalEntryDto(
            id = entry.id,
            userId = entry.userId,
            title = entry.title,
            content = entry.content,
            timestamp = entry.timestamp
        )
    }
}
