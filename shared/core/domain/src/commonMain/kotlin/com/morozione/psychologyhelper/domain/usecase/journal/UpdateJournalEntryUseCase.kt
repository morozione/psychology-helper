package com.morozione.psychologyhelper.domain.usecase.journal

import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.domain.repository.JournalRepository

class UpdateJournalEntryUseCase(private val journalRepository: JournalRepository) {
    suspend operator fun invoke(entry: JournalEntry): Result<Unit> =
        journalRepository.updateJournalEntry(entry)
}
