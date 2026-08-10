package com.morozione.psychologyhelper.feature.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RegisterIntent {
    data class UpdateDisplayName(val value: String) : RegisterIntent()
    data class UpdateEmail(val value: String) : RegisterIntent()
    data class UpdatePassword(val value: String) : RegisterIntent()
    object Submit : RegisterIntent()
    object Retry : RegisterIntent()
}

sealed class RegisterEffect {
    data class ShowError(val message: String) : RegisterEffect()
}

class RegisterScreenModel(
    private val registerUseCase: RegisterUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<RegisterEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<RegisterEffect> = _effects.asSharedFlow()

    fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.UpdateDisplayName -> reduce { copy(displayName = intent.value) }
            is RegisterIntent.UpdateEmail -> reduce { copy(email = intent.value) }
            is RegisterIntent.UpdatePassword -> reduce { copy(password = intent.value) }
            is RegisterIntent.Submit -> register()
            is RegisterIntent.Retry -> reduce { copy(error = null) }
        }
    }

    private fun register() {
        val name = _state.value.displayName.trim()
        val email = _state.value.email.trim()
        val password = _state.value.password
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            val msg = "All fields are required"
            reduce { copy(error = msg) }
            screenModelScope.launch { _effects.emit(RegisterEffect.ShowError(msg)) }
            return
        }
        if (password.length < 6) {
            val msg = "Password must be at least 6 characters"
            reduce { copy(error = msg) }
            screenModelScope.launch { _effects.emit(RegisterEffect.ShowError(msg)) }
            return
        }
        screenModelScope.launch {
            reduce { copy(isLoading = true, error = null) }
            val result = registerUseCase(email, password, name)
            if (result.isSuccess) {
                // App.kt observes auth state and switches to HomeScreen automatically on register success.
                reduce { copy(isLoading = false) }
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Registration failed"
                reduce { copy(isLoading = false, error = msg) }
                _effects.emit(RegisterEffect.ShowError(msg))
            }
        }
    }

    private fun reduce(reducer: RegisterState.() -> RegisterState) {
        _state.update { it.reducer() }
    }
}
