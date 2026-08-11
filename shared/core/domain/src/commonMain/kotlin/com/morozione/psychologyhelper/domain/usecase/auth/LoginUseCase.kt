package com.morozione.psychologyhelper.domain.usecase.auth

import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.login(email, password)
}
