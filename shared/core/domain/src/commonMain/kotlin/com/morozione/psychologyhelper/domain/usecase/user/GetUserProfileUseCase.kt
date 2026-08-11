package com.morozione.psychologyhelper.domain.usecase.user

import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(private val userRepository: UserRepository) {
    operator fun invoke(userId: String): Flow<User> = userRepository.getUserProfile(userId)
}
