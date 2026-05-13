# Feature: Profile

## Status: planned

## Description
User profile screen showing account info and app settings.

## User Stories
- [ ] As a user, I can view my profile information
- [ ] As a user, I can see my Google account details
- [ ] As a user, I can sign out from my account
- [ ] As a user, I can see my problem-solving statistics

## Screens

### Screen: Profile
- **Route**: `/profile`
- **Components**:
  - Profile picture (from Google)
  - Display name
  - Email
  - Statistics card (problems solved, active problems, streak)
  - Sign out button
- **Behavior**:
  - Accessible from Dashboard navigation
  - Sign out returns to Welcome screen

## Data Model
```kotlin
data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val createdAt: Timestamp,
    val stats: UserStats
)

data class UserStats(
    val totalProblems: Int,
    val solvedProblems: Int,
    val activeProblems: Int,
    val currentStreak: Int,  // days
    val longestStreak: Int
)
```

## Firebase Collection
```
users/{uid}
  - email: String
  - displayName: String
  - photoUrl: String?
  - createdAt: Timestamp
  - stats: Map
```

## Implementation Notes
- Create user document on first sign-in
- Update stats when problems change status
- Profile picture loaded from Google account URL
