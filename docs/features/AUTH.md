# Feature: Authentication

## Status: planned

## Description
Users authenticate using Google Sign-In via Firebase Authentication. No email/password or other auth methods.

## User Stories
- [ ] As a user, I can sign in with my Google account
- [ ] As a user, I can sign out
- [ ] As a user, I remain signed in between app sessions

## Screens

### Screen: Welcome/Login
- **Route**: `/auth`
- **Components**:
  - App logo
  - App tagline
  - "Sign in with Google" button
- **Behavior**:
  - Show on app launch if not authenticated
  - Redirect to Dashboard after successful sign-in

## Data Model
```kotlin
// Firebase Auth provides User object
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
```

## Dependencies
- Firebase Auth SDK
- Google Sign-In SDK

## Implementation Notes
- Use Firebase Auth's Google Sign-In provider
- Store auth state in shared preferences for session persistence
- Handle token refresh automatically via Firebase SDK
