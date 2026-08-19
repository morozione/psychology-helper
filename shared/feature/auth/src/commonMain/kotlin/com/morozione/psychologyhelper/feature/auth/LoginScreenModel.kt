package com.morozione.psychologyhelper.feature.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.usecase.auth.LoginUseCase
import com.morozione.psychologyhelper.domain.usecase.auth.LoginWithGoogleUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginIntent {
    data class UpdateEmail(val value: String) : LoginIntent()
    data class UpdatePassword(val value: String) : LoginIntent()
    object Submit : LoginIntent()
    object Retry : LoginIntent()
    data class GoogleSignInResult(val idToken: String?, val error: String?) : LoginIntent()
}

sealed class LoginEffect {
    data class ShowError(val message: String) : LoginEffect()
}

class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LoginEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<LoginEffect> = _effects.asSharedFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateEmail -> reduce { copy(email = intent.value) }
            is LoginIntent.UpdatePassword -> reduce { copy(password = intent.value) }
            is LoginIntent.Submit -> login()
            is LoginIntent.Retry -> reduce { copy(error = null) }
            is LoginIntent.GoogleSignInResult -> onGoogleSignInResult(intent.idToken, intent.error)
        }
    }

    private fun onGoogleSignInResult(idToken: String?, error: String?) {
        if (idToken == null) {
            val msg = error ?: "Google sign-in failed"
            reduce { copy(error = msg) }
            screenModelScope.launch { _effects.emit(LoginEffect.ShowError(msg)) }
            return
        }
        screenModelScope.launch {
            reduce { copy(isLoading = true, error = null) }
            val result = loginWithGoogleUseCase(idToken)
            if (result.isSuccess) {
                // App.kt observes auth state changes and navigates to HomeScreen automatically.
                reduce { copy(isLoading = false) }
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Google sign-in failed"
                reduce { copy(isLoading = false, error = msg) }
                _effects.emit(LoginEffect.ShowError(msg))
            }
        }
    }

    private fun login() {
        val email = _state.value.email.trim()
        val password = _state.value.password
        if (email.isBlank() || password.isBlank()) {
            val msg = "Email and password cannot be empty"
            reduce { copy(error = msg) }
            screenModelScope.launch { _effects.emit(LoginEffect.ShowError(msg)) }
            return
        }
        screenModelScope.launch {
            reduce { copy(isLoading = true, error = null) }
            val result = loginUseCase(email, password)
            if (result.isSuccess) {
                // App.kt observes auth state changes and navigates to HomeScreen automatically.
                reduce { copy(isLoading = false) }
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Login failed"
                reduce { copy(isLoading = false, error = msg) }
                _effects.emit(LoginEffect.ShowError(msg))
            }
        }
    }

    private fun reduce(reducer: LoginState.() -> LoginState) {
        _state.update { it.reducer() }
    }
}
