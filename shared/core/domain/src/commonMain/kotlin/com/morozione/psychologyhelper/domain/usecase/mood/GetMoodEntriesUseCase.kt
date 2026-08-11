package com.morozione.psychologyhelper.domain.usecase.mood

import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.repository.MoodRepository
import kotlinx.coroutines.flow.Flow

class GetMoodEntriesUseCase(private val moodRepository: MoodRepository) {
    operator fun invoke(userId: String): Flow<List<MoodEntry>> =
        moodRepository.getMoodEntries(userId)
}
