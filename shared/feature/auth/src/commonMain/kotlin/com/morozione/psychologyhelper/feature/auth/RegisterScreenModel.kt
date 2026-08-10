package com.morozione.psychologyhelper.feature.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    data object Idle : RegisterState()
    data object Loading : RegisterState()
    data object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterScreenModel(
    private val registerUseCase: RegisterUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onDisplayNameChanged(value: String) { _displayName.value = value }
    fun onEmailChanged(value: String) { _email.value = value }
    fun onPasswordChanged(value: String) { _password.value = value }

    fun register() {
        val name = _displayName.value.trim()
        val email = _email.value.trim()
        val password = _password.value
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = RegisterState.Error("All fields are required")
            return
        }
        if (password.length < 6) {
            _state.value = RegisterState.Error("Password must be at least 6 characters")
            return
        }
        screenModelScope.launch {
            _state.value = RegisterState.Loading
            val result = registerUseCase(email, password, name)
            _state.value = if (result.isSuccess) {
                RegisterState.Success
            } else {
                RegisterState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun clearError() {
        if (_state.value is RegisterState.Error) {
            _state.value = RegisterState.Idle
        }
    }
}
