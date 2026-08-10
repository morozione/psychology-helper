package com.morozione.psychologyhelper.feature.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.usecase.auth.GetCurrentUserUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: User? = null,
    val recentMoodEntries: List<MoodEntry> = emptyList(),
    val isLoading: Boolean = true
)

class HomeScreenModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            getCurrentUserUseCase().filterNotNull().collectLatest { user ->
                _state.value = _state.value.copy(user = user, isLoading = false)
                getMoodEntriesUseCase(user.id).collectLatest { entries ->
                    _state.value = _state.value.copy(
                        recentMoodEntries = entries.take(3)
                    )
                }
            }
        }
    }
}
