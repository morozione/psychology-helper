package com.morozione.psychologyhelper.domain.usecase.journal

import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow

class GetJournalEntriesUseCase(private val journalRepository: JournalRepository) {
    operator fun invoke(userId: String): Flow<List<JournalEntry>> =
        journalRepository.getJournalEntries(userId)
}
