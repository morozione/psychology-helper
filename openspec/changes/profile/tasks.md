# Tasks: User Profile

## Data Layer
- [ ] Create `UserProfile` data class
- [ ] Create `UserStats` data class
- [ ] Create `UserRepository` interface
- [ ] Implement `UserRepositoryImpl`
- [ ] Create user document on first sign-in
- [ ] Implement stats calculation from problems

## Domain Layer
- [ ] Create `GetUserProfileUseCase`
- [ ] Create `CalculateStatsUseCase`
- [ ] Create `SignOutUseCase` (if not already done in auth)

## UI Layer
- [ ] Create `ProfileScreen` composable
- [ ] Create `ProfileViewModel`
- [ ] Create `ProfileHeader` component (avatar, name, email)
- [ ] Create `StatsCard` component
- [ ] Create `StatItem` component
- [ ] Create `SignOutButton` component
- [ ] Handle loading state
- [ ] Confirm sign-out with dialog

## Navigation
- [ ] Add profile route
- [ ] Add to bottom navigation
- [ ] Navigate to Welcome on sign-out

## Testing
- [ ] Test profile data display
- [ ] Test stats calculation
- [ ] Test sign-out flow
