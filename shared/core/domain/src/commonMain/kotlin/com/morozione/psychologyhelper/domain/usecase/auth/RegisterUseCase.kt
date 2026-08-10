package com.morozione.psychologyhelper.domain.usecase.auth

import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = authRepository.register(email, password, displayName)
}
