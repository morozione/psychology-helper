# Design: Dashboard

## Technical Approach
Compose Multiplatform screen with real-time Firestore data updates.

## Architecture

### Data Flow
```
Firestore → ProblemsRepository → DashboardViewModel → DashboardScreen
```

### Components

#### UI Layout
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

### Data Model
```kotlin
data class DashboardState(
    val user: UserProfile,
    val activeProblems: List<Problem>,
    val todayCheckInCompleted: Boolean,
    val isLoading: Boolean,
    val error: String?
)
```

### ViewModel
```kotlin
class DashboardViewModel(
    private val getProblemsUseCase: GetActiveProblemsUseCase,
    private val getUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    val state: StateFlow<DashboardState>
}
```

## Screen: Dashboard
- **Route**: `/dashboard`
- **Components**:
  - `DashboardHeader` - greeting + avatar
  - `InvestigateButton` - primary CTA
  - `ProblemsList` - LazyColumn of ProblemCard
  - `BottomNavBar` - Dashboard, Problems, Profile
- **Behavior**:
  - Default screen after authentication
  - Real-time updates via Firestore listeners
  - Empty state when no problems

## Navigation
- Dashboard → Problem Investigation (button tap)
- Dashboard → Problem Detail (card tap)
- Dashboard → Profile (nav bar)
