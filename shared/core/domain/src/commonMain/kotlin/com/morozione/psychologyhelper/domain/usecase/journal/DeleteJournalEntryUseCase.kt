package com.morozione.psychologyhelper.domain.usecase.journal

import com.morozione.psychologyhelper.domain.repository.JournalRepository

class DeleteJournalEntryUseCase(private val journalRepository: JournalRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        journalRepository.deleteJournalEntry(id)
}
