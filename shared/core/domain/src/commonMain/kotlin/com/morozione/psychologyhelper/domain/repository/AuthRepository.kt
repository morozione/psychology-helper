package com.morozione.psychologyhelper.domain.repository

import com.morozione.psychologyhelper.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun register(email: String, password: String, displayName: String): Result<User>
    suspend fun logout()
}
