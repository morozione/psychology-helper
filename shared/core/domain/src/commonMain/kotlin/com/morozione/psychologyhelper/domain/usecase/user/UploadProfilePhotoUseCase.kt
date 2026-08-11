package com.morozione.psychologyhelper.domain.usecase.user

import com.morozione.psychologyhelper.domain.repository.UserRepository

class UploadProfilePhotoUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String, imageBytes: ByteArray): Result<String> =
        userRepository.uploadProfilePhoto(userId, imageBytes)
}
