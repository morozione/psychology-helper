package com.morozione.psychologyhelper.domain.usecase.user

import com.morozione.psychologyhelper.domain.repository.UserRepository

class UpdateProfilePhotoUrlUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String, url: String): Result<Unit> =
        userRepository.updateProfilePhotoUrl(userId, url)
}
