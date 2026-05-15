# Design: Daily Check-in

## Technical Approach
Question-based flow with AI-generated questions contextual to user's active problems.

## Architecture

### Data Flow
```
CheckInScreen → CheckInViewModel → GeminiRepository → Gemini API
                                 → CheckInRepository → Firestore
```

### Check-in Flow
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

### Data Models
```kotlin
data class DailyCheckIn(
    val id: String,
    val uid: String,
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

### Firebase Collection
```
users/{uid}/checkins/{date}
  - date: String (YYYY-MM-DD)
  - questions: Array<Map>
    - question: String
    - answer: String
    - timestamp: Timestamp
  - moodScore: Number?
  - completedAt: Timestamp
```

### Gemini Question Prompt
```
Generate a daily check-in question for a user working on these problems:
Active problems: {problem_list}
Last check-in mood: {last_mood}
Days since last check-in: {days}

Guidelines:
- Ask about feelings, progress, or challenges
- Be warm and supportive
- Keep questions open-ended but focused
- Vary questions day to day
```

## Screens

### Screen: Daily Check-in
- **Route**: `/checkin`
- **Components**:
  - Current question card
  - Text input for response
  - Progress dots (1/3, 2/3, 3/3)
  - Mood selector (1-5 scale, optional)
  - Skip button
- **Behavior**:
  - Questions appear one at a time
  - AI generates follow-up based on responses
  - Completion triggers streak update

### Screen: Check-in History
- **Route**: `/checkin/history`
- **Components**:
  - Calendar view with check-in indicators
  - List of past check-ins
  - Streak indicator
- **Behavior**:
  - Tap date to view that day's check-in
  - Visual streak display

## Notifications
- Daily reminder at user-set time (default: 9 AM)
- "Time for your daily check-in!"
- Respects user's notification preferences
