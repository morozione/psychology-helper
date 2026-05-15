# Tasks: Problem Tracking

## Data Layer
- [ ] Extend `Problem` data class with full fields
- [ ] Create `Hint` data class
- [ ] Create `ProblemStatus` enum
- [ ] Extend `ProblemsRepository` with CRUD operations
- [ ] Implement hints subcollection handling
- [ ] Create hint generation via Gemini

## Domain Layer
- [ ] Create `GetAllProblemsUseCase`
- [ ] Create `GetProblemByIdUseCase`
- [ ] Create `UpdateProblemStatusUseCase`
- [ ] Create `GenerateHintUseCase`
- [ ] Create `SaveHintFeedbackUseCase`

## UI Layer - Problems List
- [ ] Create `ProblemsListScreen` composable
- [ ] Create `ProblemsViewModel`
- [ ] Create `FilterTabs` component (All, Active, Resolved)
- [ ] Create `ProblemCard` with status badge
- [ ] Add FAB for new investigation
- [ ] Handle empty states per filter

## UI Layer - Problem Detail
- [ ] Create `ProblemDetailScreen` composable
- [ ] Create `ProblemDetailViewModel`
- [ ] Create `StatusSelector` dropdown
- [ ] Create `TodayHintCard` component
- [ ] Create `HintHistoryList` component
- [ ] Create `HintFeedback` buttons (helpful/not)
- [ ] Create "Get New Hint" button
- [ ] Handle loading state for hint generation

## Navigation
- [ ] Add problems list route
- [ ] Add problem detail route with id param
- [ ] Navigate from Dashboard to Problems List
- [ ] Navigate from Problems List to Detail

## Notifications (Future)
- [ ] Set up WorkManager (Android)
- [ ] Set up BackgroundTasks (iOS)
- [ ] Schedule daily hint notifications
- [ ] Handle notification tap → Problem Detail

## Testing
- [ ] Test problems list filtering
- [ ] Test status updates
- [ ] Test hint generation
- [ ] Test hint feedback saving
