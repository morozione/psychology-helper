# Tasks: Daily Check-in

## Data Layer
- [ ] Create `DailyCheckIn` data class
- [ ] Create `CheckInQuestion` data class
- [ ] Create `CheckInRepository` interface
- [ ] Implement `CheckInRepositoryImpl`
- [ ] Store check-ins by date (YYYY-MM-DD)
- [ ] Create question generation via Gemini

## Domain Layer
- [ ] Create `GetTodayCheckInUseCase`
- [ ] Create `GenerateCheckInQuestionUseCase`
- [ ] Create `SaveCheckInAnswerUseCase`
- [ ] Create `CompleteCheckInUseCase`
- [ ] Create `GetCheckInHistoryUseCase`
- [ ] Create `UpdateStreakUseCase`

## UI Layer - Check-in Screen
- [ ] Create `CheckInScreen` composable
- [ ] Create `CheckInViewModel`
- [ ] Create `QuestionCard` component
- [ ] Create `AnswerInput` component
- [ ] Create `ProgressDots` component
- [ ] Create `MoodSelector` component (1-5)
- [ ] Create `SkipButton` component
- [ ] Handle question transitions
- [ ] Show completion celebration

## UI Layer - History Screen
- [ ] Create `CheckInHistoryScreen` composable
- [ ] Create `HistoryViewModel`
- [ ] Create `CalendarView` component
- [ ] Create `CheckInListItem` component
- [ ] Create `StreakBadge` component
- [ ] Handle empty state

## Navigation
- [ ] Add check-in routes
- [ ] Show check-in prompt on Dashboard if not completed
- [ ] Navigate to check-in from prompt
- [ ] Navigate to history from profile or nav

## Notifications
- [ ] Set up daily notification scheduling
- [ ] Create notification channel (Android)
- [ ] Handle notification tap → Check-in screen
- [ ] Add notification preferences (time selection)

## Testing
- [ ] Test question generation
- [ ] Test answer saving
- [ ] Test streak calculation
- [ ] Test calendar display
- [ ] Test notification delivery
