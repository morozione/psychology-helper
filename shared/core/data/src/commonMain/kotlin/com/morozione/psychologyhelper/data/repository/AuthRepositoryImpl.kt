package com.morozione.psychologyhelper.data.repository

import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(private val auth: FirebaseAuth) : AuthRepository {

    override val currentUser: Flow<User?> = auth.authStateChanged.map { firebaseUser ->
        firebaseUser?.toDomain()
    }

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password)
        result.user?.toDomain() ?: error("Login failed: user is null")
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password)
        val user = result.user ?: error("Registration failed: user is null")
        user.updateProfile(displayName = displayName)
        User(
            id = user.uid,
            email = user.email ?: email,
            displayName = displayName
        )
    }

    override suspend fun logout() {
        auth.signOut()
    }

    private fun FirebaseUser.toDomain(): User = User(
        id = uid,
        email = email ?: "",
        displayName = displayName ?: ""
    )
}
