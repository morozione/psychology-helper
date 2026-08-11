package com.morozione.psychologyhelper.domain.usecase.mood

import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.repository.MoodRepository

class AddMoodEntryUseCase(private val moodRepository: MoodRepository) {
    suspend operator fun invoke(entry: MoodEntry): Result<Unit> =
        moodRepository.addMoodEntry(entry)
}
