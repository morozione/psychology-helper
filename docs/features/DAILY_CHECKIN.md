# Feature: Daily Check-in

## Status: planned

## Description
Daily AI-guided check-in where the app asks questions about user's progress and emotional state. Helps track improvement over time.

## User Stories
- [ ] As a user, I receive a daily check-in prompt
- [ ] As a user, I answer AI questions about my day/progress
- [ ] As a user, I can see my check-in history
- [ ] As a user, I can see patterns in my responses over time

## Flow
```
1. User opens app or receives notification
2. If daily check-in not completed, prompt appears
3. AI asks 2-3 questions about:
   - How they're feeling today
   - Progress on active problems
   - Any new concerns
4. User responds to each question
5. AI provides brief encouragement/insight
6. Check-in marked as complete for the day
```

## Screens

### Screen: Daily Check-in
- **Route**: `/checkin`
- **Components**:
  - Current question card
  - Text input for response
  - Progress dots (question 1/3, 2/3, 3/3)
  - Skip option
- **Behavior**:
  - Questions appear one at a time
  - AI generates follow-up based on responses
  - Completion triggers streak update

### Screen: Check-in History
- **Route**: `/checkin/history`
- **Components**:
  - Calendar view with check-in indicators
  - List of past check-ins
  - Mood/progress trends chart
- **Behavior**:
  - Tap date to view that day's check-in
  - Visual streak indicator

## Data Model
```kotlin
data class DailyCheckIn(
    val id: String,
    val odId: String,
    val date: LocalDate,
    val questions: List<CheckInQuestion>,
    val moodScore: Int?,  // 1-5 scale
    val completedAt: Timestamp
)

data class CheckInQuestion(
    val question: String,
    val answer: String,
    val timestamp: Timestamp
)
```

## Firebase Collection
```
users/{odId}/checkins/{date}
  - date: String (YYYY-MM-DD)
  - questions: Array<Map>
  - moodScore: Number?
  - completedAt: Timestamp
```

## Gemini API - Check-in Questions
```kotlin
val checkInPrompt = """
Generate a daily check-in question for a user working on these problems:
Active problems: {problem_list}
Last check-in mood: {last_mood}
Days since last check-in: {days}

Guidelines:
- Ask about feelings, progress, or challenges
- Be warm and supportive
- Keep questions open-ended but focused
- Vary questions day to day
"""
```

## Notifications
- Daily reminder at user-set time (default: 9 AM)
- "Time for your daily check-in!"
- Respects user's notification preferences

## Implementation Notes
- One check-in per calendar day
- Store locally first, sync to Firestore
- Track streak for gamification
- Generate questions contextually based on active problems
