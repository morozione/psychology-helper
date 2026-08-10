package com.morozione.psychologyhelper.data.repository

import com.morozione.psychologyhelper.data.dto.MoodEntryDto
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.repository.MoodRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MoodRepositoryImpl(private val firestore: FirebaseFirestore) : MoodRepository {

    private fun moodsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("moods")

    override fun getMoodEntries(userId: String): Flow<List<MoodEntry>> =
        moodsCollection(userId).snapshots().map { querySnapshot ->
            querySnapshot.documents.mapNotNull { doc ->
                runCatching { doc.data<MoodEntryDto>().toDomain() }.getOrNull()
            }.sortedByDescending { it.timestamp }
        }

    override suspend fun addMoodEntry(entry: MoodEntry): Result<Unit> = runCatching {
        val dto = MoodEntryDto.fromDomain(entry)
        moodsCollection(entry.userId).document(entry.id).set(dto)
    }

    override suspend fun deleteMoodEntry(id: String): Result<Unit> = runCatching {
        // We need userId context; for delete we search all users — callers must pass userId/id pair.
        // As a workaround, id is stored as the document id under the authenticated user's sub-collection.
        // This requires the caller to also know userId. We use a composite id: "userId::docId".
        val parts = id.split("::")
        if (parts.size == 2) {
            val (userId, docId) = parts
            moodsCollection(userId).document(docId).delete()
        }
    }
}
