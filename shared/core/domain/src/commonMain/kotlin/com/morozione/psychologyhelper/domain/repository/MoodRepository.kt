package com.morozione.psychologyhelper.domain.repository

import com.morozione.psychologyhelper.domain.entity.MoodEntry
import kotlinx.coroutines.flow.Flow

interface MoodRepository {
    fun getMoodEntries(userId: String): Flow<List<MoodEntry>>
    suspend fun addMoodEntry(entry: MoodEntry): Result<Unit>
    suspend fun deleteMoodEntry(id: String): Result<Unit>
}
