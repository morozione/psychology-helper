package com.morozione.psychologyhelper.data.dto

import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import kotlinx.serialization.Serializable

@Serializable
data class MoodEntryDto(
    val id: String = "",
    val userId: String = "",
    val mood: String = "",
    val note: String = "",
    val timestamp: Long = 0L
) {
    fun toDomain(): MoodEntry = MoodEntry(
        id = id,
        userId = userId,
        mood = runCatching { Mood.valueOf(mood) }.getOrDefault(Mood.OKAY),
        note = note,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(entry: MoodEntry): MoodEntryDto = MoodEntryDto(
            id = entry.id,
            userId = entry.userId,
            mood = entry.mood.name,
            note = entry.note,
            timestamp = entry.timestamp
        )
    }
}
