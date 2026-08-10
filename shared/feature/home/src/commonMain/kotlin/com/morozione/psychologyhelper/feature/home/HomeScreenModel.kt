package com.morozione.psychologyhelper.feature.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.usecase.auth.GetCurrentUserUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val user: User? = null,
    val recentMoodEntries: List<MoodEntry> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class HomeIntent {
    object LoadData : HomeIntent()
    object Retry : HomeIntent()
}

sealed class HomeEffect {
    data class ShowError(val message: String) : HomeEffect()
}

class HomeScreenModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<HomeEffect> = _effects.asSharedFlow()

    init {
        onIntent(HomeIntent.LoadData)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadData -> loadData()
            is HomeIntent.Retry -> {
                reduce { copy(error = null, isLoading = true) }
                loadData()
            }
        }
    }

    private fun loadData() {
        screenModelScope.launch {
            getCurrentUserUseCase().filterNotNull().collectLatest { user ->
                reduce { copy(user = user, isLoading = false) }
                getMoodEntriesUseCase(user.id).collectLatest { entries ->
                    reduce { copy(recentMoodEntries = entries.take(3)) }
                }
            }
        }
    }

    private fun reduce(reducer: HomeState.() -> HomeState) {
        _state.update { it.reducer() }
    }
}
