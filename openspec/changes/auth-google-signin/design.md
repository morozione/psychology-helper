# Design: Google Sign-In Authentication

## Technical Approach
Use Firebase Authentication with Google Sign-In provider on both Android and iOS via Kotlin Multiplatform.

## Architecture

### Data Flow
```
UI (Compose) → AuthRepository → Firebase Auth SDK → Google Sign-In
                    ↓
              Firestore (user doc)
```

### Components

#### Data Layer
```kotlin
// AuthRepository.kt
interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    suspend fun signInWithGoogle(): Result<AuthUser>
    suspend fun signOut()
}

// AuthUser.kt
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
```

#### Domain Layer
```kotlin
// SignInUseCase.kt
class SignInWithGoogleUseCase(private val authRepo: AuthRepository)

// SignOutUseCase.kt
class SignOutUseCase(private val authRepo: AuthRepository)
```

#### UI Layer
```kotlin
// AuthScreen.kt - Welcome screen with sign-in button
// AuthViewModel.kt - Handles auth state
```

## Screen: Welcome/Login
- **Route**: `/auth`
- **Components**:
  - App logo (centered)
  - App tagline
  - "Sign in with Google" button (Material3)
- **Behavior**:
  - Show on app launch if not authenticated
  - Redirect to Dashboard after successful sign-in

## Platform-Specific Implementation

### Android
- Use `GoogleSignInClient` via Firebase
- Activity result launcher for sign-in intent

### iOS
- Use `GIDSignIn` via Firebase
- Present sign-in view controller

## Firebase Collection
```
users/{uid}
  - email: String
  - displayName: String
  - photoUrl: String?
  - createdAt: Timestamp
```

## Dependencies
- `firebase-auth` (KMP)
- `google-signin` (platform-specific)
