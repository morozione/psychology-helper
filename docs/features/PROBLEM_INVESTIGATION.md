# Feature: Problem Investigation

## Status: planned

## Description
AI-powered chat to help users identify and articulate their problems. A short conversation (3-4 messages) that ends with a summary and generates a list of topics to work on.

## User Stories
- [ ] As a user, I can describe my problem in a chat
- [ ] As a user, I receive helpful questions from AI to clarify my problem
- [ ] As a user, I get a summary after 3-4 exchanges
- [ ] As a user, I see a list of identified topics/problems to solve

## Flow
```
1. User clicks "Investigate Problem" on Dashboard
2. AI greets and asks user to describe what's bothering them
3. User explains their situation
4. AI asks clarifying question (1-2 more exchanges)
5. AI provides summary and identifies problem topics
6. User confirms or adjusts the identified problems
7. Problems are saved with "active" status
```

## Screens

### Screen: Investigation Chat
- **Route**: `/investigate`
- **Components**:
  - Chat message list
  - Text input with send button
  - Progress indicator (message count)
  - Summary card (appears at end)
- **Behavior**:
  - Limited to ~4 user messages
  - AI analyzes and generates summary automatically
  - Shows identified problems as checklist

### Screen: Problem Confirmation
- **Route**: `/investigate/confirm`
- **Components**:
  - Summary text
  - List of identified problems (checkboxes)
  - "Save Problems" button
- **Behavior**:
  - User can deselect problems they don't want to track
  - Save creates Problem entries in Firestore

## Data Model
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
```

## Gemini API Integration
```kotlin
// System prompt for problem investigation
val systemPrompt = """
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
"""
```

## Implementation Notes
- Use Gemini 2.5 free API
- Track message count to trigger summary
- Parse AI response for SUMMARY and PROBLEMS sections
- Store session in Firestore for history
