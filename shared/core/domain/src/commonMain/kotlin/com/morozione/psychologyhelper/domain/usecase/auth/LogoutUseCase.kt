package com.morozione.psychologyhelper.domain.usecase.auth

import com.morozione.psychologyhelper.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.logout()
}
