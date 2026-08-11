package com.morozione.psychologyhelper.domain.repository

import com.morozione.psychologyhelper.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun uploadProfilePhoto(userId: String, imageBytes: ByteArray): Result<String>
    suspend fun updateProfilePhotoUrl(userId: String, url: String): Result<Unit>
    fun getUserProfile(userId: String): Flow<User>
}
