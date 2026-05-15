# Tasks: Dashboard

## Data Layer
- [ ] Create `Problem` data class
- [ ] Create `ProblemsRepository` interface
- [ ] Implement Firestore problems fetching
- [ ] Add real-time listener for problems updates

## Domain Layer
- [ ] Create `GetActiveProblemsUseCase`
- [ ] Create `GetUserProfileUseCase`

## UI Layer
- [ ] Create `DashboardScreen` composable
- [ ] Create `DashboardViewModel`
- [ ] Create `DashboardHeader` component (greeting + avatar)
- [ ] Create `InvestigateButton` component
- [ ] Create `ProblemCard` component
- [ ] Create `ProblemsList` component (LazyColumn)
- [ ] Create `EmptyProblemsState` component
- [ ] Handle loading state
- [ ] Handle error state

## Navigation
- [ ] Create `BottomNavBar` component
- [ ] Set up dashboard as home destination
- [ ] Add navigation to Problem Investigation
- [ ] Add navigation to Problem Detail
- [ ] Add navigation to Profile

## Testing
- [ ] Test problems list display
- [ ] Test empty state
- [ ] Test navigation flows
- [ ] Test real-time updates
