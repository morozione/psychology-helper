# Design: Problem Tracking

## Technical Approach
List and detail screens with Firestore real-time updates. AI hint generation via Gemini.

## Architecture

### Data Flow
```
ProblemsScreen → ProblemsViewModel → ProblemsRepository → Firestore
ProblemDetailScreen → DetailViewModel → GeminiRepository → Gemini API
```

### Problem Statuses
| Status | Description |
|--------|-------------|
| `active` | Currently working on this problem |
| `in_progress` | Making progress, partially addressed |
| `paused` | Temporarily not focusing on this |
| `resolved` | Problem has been solved |

### Data Models
```kotlin
data class Problem(
    val id: String,
    val odId: String,
    val title: String,
    val description: String,
    val status: ProblemStatus,
    val hints: List<Hint>,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val resolvedAt: Timestamp?
)

data class Hint(
    val id: String,
    val content: String,
    val createdAt: Timestamp,
    val wasHelpful: Boolean?
)

enum class ProblemStatus {
    ACTIVE, IN_PROGRESS, PAUSED, RESOLVED
}
```

### Firebase Collection
```
users/{uid}/problems/{problemId}
  - title: String
  - description: String
  - status: String
  - createdAt: Timestamp
  - updatedAt: Timestamp
  - resolvedAt: Timestamp?

users/{uid}/problems/{problemId}/hints/{hintId}
  - content: String
  - createdAt: Timestamp
  - wasHelpful: Boolean?
```

### Gemini Hint Prompt
```
Generate a helpful, actionable hint for someone working on this problem:
Problem: {problem_title}
Description: {problem_description}
Previous hints: {previous_hints}

Guidelines:
- Be specific and actionable
- Suggest one small step they can take today
- Be encouraging but realistic
- Keep it to 2-3 sentences
```

## Screens

### Screen: Problems List
- **Route**: `/problems`
- **Components**:
  - Filter tabs (All, Active, Resolved)
  - Problem cards with status badge
  - FAB to start new investigation
- **Behavior**:
  - Tap card to open Problem Detail
  - Filter by status

### Screen: Problem Detail
- **Route**: `/problems/{id}`
- **Components**:
  - Problem title and description
  - Status selector dropdown
  - Today's hint card
  - Hint history list
  - "Get New Hint" button
  - Hint feedback (helpful/not helpful)
- **Behavior**:
  - Daily hint generated automatically
  - Can manually request new hint

## Notifications
- Daily notification at user-preferred time
- Contains hint for one active problem (rotating)
- Tap opens Problem Detail screen
