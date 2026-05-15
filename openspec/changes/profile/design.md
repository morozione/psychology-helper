# Design: User Profile

## Technical Approach
Simple profile screen with data from Firebase Auth and computed statistics from Firestore.

## Architecture

### Data Flow
```
ProfileScreen → ProfileViewModel → UserRepository → Firebase Auth
                                 → ProblemsRepository → Firestore (stats)
```

### Data Models
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

### Firebase Collection
```
users/{uid}
  - email: String
  - displayName: String
  - photoUrl: String?
  - createdAt: Timestamp
  - stats: Map
    - totalProblems: Number
    - solvedProblems: Number
    - activeProblems: Number
    - currentStreak: Number
    - longestStreak: Number
```

## Screen: Profile
- **Route**: `/profile`
- **Components**:
  - Profile picture (AsyncImage from Google URL)
  - Display name (large text)
  - Email (secondary text)
  - Statistics card:
    - Total problems
    - Solved problems
    - Active problems
    - Current streak
  - Sign out button (destructive style)
- **Behavior**:
  - Accessible from bottom navigation
  - Sign out returns to Welcome screen
  - Stats update in real-time

## Statistics Calculation
- `totalProblems`: Count of all problems
- `solvedProblems`: Count where status = RESOLVED
- `activeProblems`: Count where status = ACTIVE or IN_PROGRESS
- `currentStreak`: Consecutive days with check-in
- `longestStreak`: Maximum streak achieved
