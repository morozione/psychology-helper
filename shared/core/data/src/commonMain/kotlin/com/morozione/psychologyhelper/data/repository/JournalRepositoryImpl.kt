package com.morozione.psychologyhelper.data.repository

import com.morozione.psychologyhelper.data.dto.JournalEntryDto
import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.domain.repository.JournalRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JournalRepositoryImpl(private val firestore: FirebaseFirestore) : JournalRepository {

    private fun journalCollection(userId: String) =
        firestore.collection("users").document(userId).collection("journal")

    override fun getJournalEntries(userId: String): Flow<List<JournalEntry>> =
        journalCollection(userId).snapshots().map { querySnapshot ->
            querySnapshot.documents.mapNotNull { doc ->
                runCatching { doc.data<JournalEntryDto>().toDomain() }.getOrNull()
            }.sortedByDescending { it.timestamp }
        }

    override suspend fun addJournalEntry(entry: JournalEntry): Result<Unit> = runCatching {
        val dto = JournalEntryDto.fromDomain(entry)
        journalCollection(entry.userId).document(entry.id).set(dto)
    }

    override suspend fun deleteJournalEntry(id: String): Result<Unit> = runCatching {
        val parts = id.split("::")
        if (parts.size == 2) {
            val (userId, docId) = parts
            journalCollection(userId).document(docId).delete()
        }
    }

    override suspend fun updateJournalEntry(entry: JournalEntry): Result<Unit> = runCatching {
        val dto = JournalEntryDto.fromDomain(entry)
        journalCollection(entry.userId).document(entry.id).set(dto)
    }
}
