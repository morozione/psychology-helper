# Feature: Dashboard

## Status: planned

## Description
Main screen after login. Shows overview of user's problems and provides entry point to investigate new problems.

## User Stories
- [ ] As a user, I can see my active problems list
- [ ] As a user, I can start investigating a new problem
- [ ] As a user, I can navigate to my profile
- [ ] As a user, I can tap on a problem to see details

## Screens

### Screen: Dashboard
- **Route**: `/dashboard`
- **Components**:
  - Header with greeting and profile avatar
  - "Investigate Problem" primary button
  - Active problems list (cards)
  - Bottom navigation (Dashboard, Problems, Profile)
- **Behavior**:
  - Default screen after authentication
  - "Investigate Problem" opens Problem Investigation chat
  - Problem card tap opens Problem Detail

## Data Model
```kotlin
data class DashboardState(
    val user: UserProfile,
    val activeProblems: List<Problem>,
    val todayCheckInCompleted: Boolean
)
```

## UI Layout
```
┌─────────────────────────────┐
│ Hello, {name}!    [avatar]  │
├─────────────────────────────┤
│                             │
│  ┌───────────────────────┐  │
│  │  Investigate Problem  │  │
│  │       [Button]        │  │
│  └───────────────────────┘  │
│                             │
│  Your Problems              │
│  ┌───────────────────────┐  │
│  │ Problem 1      status │  │
│  └───────────────────────┘  │
│  ┌───────────────────────┐  │
│  │ Problem 2      status │  │
│  └───────────────────────┘  │
│                             │
├─────────────────────────────┤
│ [Dashboard] [Problems] [Me] │
└─────────────────────────────┘
```

## Implementation Notes
- Load problems from Firestore on screen entry
- Real-time updates using Firestore listeners
- Show empty state if no problems yet
