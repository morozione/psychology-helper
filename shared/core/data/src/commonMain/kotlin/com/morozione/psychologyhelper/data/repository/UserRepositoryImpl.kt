package com.morozione.psychologyhelper.data.repository

import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.repository.UserRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
private data class UserDto(
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null
)

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun uploadProfilePhoto(
        userId: String,
        imageBytes: ByteArray
    ): Result<String> = Result.failure(UnsupportedOperationException("Photo upload not yet implemented"))

    override suspend fun updateProfilePhotoUrl(
        userId: String,
        url: String
    ): Result<Unit> = runCatching {
        firestore
            .collection("users")
            .document(userId)
            .update("photoUrl" to url)
    }

    override fun getUserProfile(userId: String): Flow<User> {
        return firestore
            .collection("users")
            .document(userId)
            .snapshots()
            .map { snapshot ->
                runCatching { snapshot.data<UserDto>() }.getOrNull()?.let { dto ->
                    User(
                        id = userId,
                        email = dto.email,
                        displayName = dto.displayName,
                        photoUrl = dto.photoUrl
                    )
                } ?: User(id = userId, email = "", displayName = "")
            }
    }
}
