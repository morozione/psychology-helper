package com.morozione.psychologyhelper.domain.repository

import com.morozione.psychologyhelper.domain.entity.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getJournalEntries(userId: String): Flow<List<JournalEntry>>
    suspend fun addJournalEntry(entry: JournalEntry): Result<Unit>
    suspend fun deleteJournalEntry(id: String): Result<Unit>
    suspend fun updateJournalEntry(entry: JournalEntry): Result<Unit>
}
