# Design: Problem Investigation

## Technical Approach
Chat UI with Gemini 2.5 API integration. Structured prompts guide conversation flow.

## Architecture

### Data Flow
```
ChatScreen → ChatViewModel → GeminiRepository → Gemini API
                  ↓
           ProblemsRepository → Firestore
```

### Conversation Flow
```
1. User clicks "Investigate Problem" on Dashboard
2. AI greets and asks user to describe what's bothering them
3. User explains their situation
4. AI asks clarifying question (1-2 more exchanges)
5. AI provides summary and identifies problem topics
6. User confirms or adjusts the identified problems
7. Problems are saved with "active" status
```

### Data Models
```kotlin
data class InvestigationSession(
    val id: String,
    val odId: String,
    val messages: List<ChatMessage>,
    val summary: String?,
    val identifiedProblems: List<String>,
    val status: SessionStatus,  // in_progress, completed
    val createdAt: Timestamp
)

data class ChatMessage(
    val id: String,
    val role: MessageRole,  // user, assistant
    val content: String,
    val timestamp: Timestamp
)

enum class SessionStatus { IN_PROGRESS, COMPLETED }
enum class MessageRole { USER, ASSISTANT }
```

### Gemini System Prompt
```
You are a supportive psychology helper. Your goal is to help the user
identify and articulate their problems clearly.

Guidelines:
- Be empathetic and non-judgmental
- Ask clarifying questions
- After 3-4 exchanges, provide a summary
- Identify 2-5 specific problem topics to work on
- Keep responses concise (2-3 sentences)

Output format for summary:
SUMMARY: [brief summary of the situation]
PROBLEMS:
- [problem 1]
- [problem 2]
- [problem 3]
```

## Screens

### Screen: Investigation Chat
- **Route**: `/investigate`
- **Components**:
  - Chat message list (LazyColumn)
  - Text input with send button
  - Progress indicator (message count: 1/4, 2/4...)
  - Loading indicator for AI response
- **Behavior**:
  - Limited to ~4 user messages
  - AI analyzes and generates summary automatically
  - Transitions to confirmation screen

### Screen: Problem Confirmation
- **Route**: `/investigate/confirm`
- **Components**:
  - Summary card
  - List of identified problems (checkboxes)
  - "Save Problems" button
- **Behavior**:
  - User can deselect problems
  - Save creates Problem entries in Firestore
  - Navigate to Dashboard on completion

## API Integration
- Parse AI response for SUMMARY: and PROBLEMS: sections
- Handle rate limits with exponential backoff
- Store conversation in Firestore for history
