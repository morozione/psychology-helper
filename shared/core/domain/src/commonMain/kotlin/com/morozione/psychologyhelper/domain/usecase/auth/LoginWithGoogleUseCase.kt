package com.morozione.psychologyhelper.domain.usecase.auth

import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.repository.AuthRepository

class LoginWithGoogleUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<User> =
        authRepository.loginWithGoogle(idToken)
}
