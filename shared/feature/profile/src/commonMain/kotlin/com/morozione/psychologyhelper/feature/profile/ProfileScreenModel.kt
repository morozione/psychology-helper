package com.morozione.psychologyhelper.feature.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.usecase.auth.LogoutUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.GetJournalEntriesUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import com.morozione.psychologyhelper.domain.usecase.user.GetUserProfileUseCase
import com.morozione.psychologyhelper.domain.usecase.user.UpdateProfilePhotoUrlUseCase
import com.morozione.psychologyhelper.domain.usecase.user.UploadProfilePhotoUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val moodEntryCount: Int = 0,
    val journalEntryCount: Int = 0,
    val isLoggingOut: Boolean = false,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val photoUrl: String? = null,
    val error: String? = null
)

sealed class ProfileIntent {
    data class Initialize(val user: User?) : ProfileIntent()
    data class UploadPhoto(val imageBytes: ByteArray) : ProfileIntent()
    object Logout : ProfileIntent()
    object Retry : ProfileIntent()
}

sealed class ProfileEffect {
    data class ShowError(val message: String) : ProfileEffect()
}

class ProfileScreenModel(
    private val logoutUseCase: LogoutUseCase,
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val getJournalEntriesUseCase: GetJournalEntriesUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val updateProfilePhotoUrlUseCase: UpdateProfilePhotoUrlUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ProfileEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ProfileEffect> = _effects.asSharedFlow()

    private var currentUserId: String = ""

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.Initialize -> initialize(intent.user)
            is ProfileIntent.UploadPhoto -> uploadPhoto(intent.imageBytes)
            is ProfileIntent.Logout -> logout()
            is ProfileIntent.Retry -> reduce { copy(error = null) }
        }
    }

    private fun initialize(user: User?) {
        val userId = user?.id ?: return
        if (currentUserId == userId) return
        currentUserId = userId
        screenModelScope.launch {
            getMoodEntriesUseCase(userId).collectLatest { entries ->
                reduce { copy(moodEntryCount = entries.size) }
            }
        }
        screenModelScope.launch {
            getJournalEntriesUseCase(userId).collectLatest { entries ->
                reduce { copy(journalEntryCount = entries.size) }
            }
        }
        screenModelScope.launch {
            getUserProfileUseCase(userId).collectLatest { profile ->
                reduce { copy(photoUrl = profile.photoUrl) }
            }
        }
    }

    private fun uploadPhoto(imageBytes: ByteArray) {
        val userId = currentUserId
        if (userId.isEmpty()) return
        screenModelScope.launch {
            reduce { copy(isUploadingPhoto = true) }
            val result = uploadProfilePhotoUseCase(userId, imageBytes)
                .mapCatching { url -> updateProfilePhotoUrlUseCase(userId, url).getOrThrow(); url }
            result.onSuccess { url ->
                reduce { copy(isUploadingPhoto = false, photoUrl = url) }
            }.onFailure { e ->
                reduce { copy(isUploadingPhoto = false) }
                _effects.emit(ProfileEffect.ShowError(e.message ?: "Failed to update photo"))
            }
        }
    }

    private fun logout() {
        screenModelScope.launch {
            reduce { copy(isLoggingOut = true) }
            logoutUseCase()
            // App.kt observes auth state change and handles navigation to LoginScreen
            reduce { copy(isLoggingOut = false) }
        }
    }

    private fun reduce(reducer: ProfileState.() -> ProfileState) {
        _state.update { it.reducer() }
    }
}
