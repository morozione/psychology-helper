# Tasks: Problem Investigation

## Setup
- [ ] Add Gemini API dependency
- [ ] Configure API key from local.properties
- [ ] Create Gemini client wrapper

## Data Layer
- [ ] Create `ChatMessage` data class
- [ ] Create `InvestigationSession` data class
- [ ] Create `GeminiRepository` interface
- [ ] Implement `GeminiRepositoryImpl` with API calls
- [ ] Create system prompt for investigation
- [ ] Implement response parsing (SUMMARY/PROBLEMS)

## Domain Layer
- [ ] Create `SendMessageUseCase`
- [ ] Create `StartInvestigationUseCase`
- [ ] Create `SaveProblemsUseCase`
- [ ] Create `ParseAiResponseUseCase`

## UI Layer - Chat Screen
- [ ] Create `InvestigationChatScreen` composable
- [ ] Create `ChatViewModel`
- [ ] Create `ChatMessageItem` component
- [ ] Create `ChatMessageList` component
- [ ] Create `ChatInput` component
- [ ] Add message count progress indicator
- [ ] Handle AI typing/loading state
- [ ] Handle error states

## UI Layer - Confirmation Screen
- [ ] Create `ProblemConfirmationScreen` composable
- [ ] Create `SummaryCard` component
- [ ] Create `ProblemCheckbox` component
- [ ] Create `ProblemsList` with toggle selection
- [ ] Create `SaveProblemsButton`
- [ ] Navigate to Dashboard on save

## Navigation
- [ ] Add investigation routes
- [ ] Navigate from Dashboard to Chat
- [ ] Navigate from Chat to Confirmation
- [ ] Navigate from Confirmation to Dashboard

## Testing
- [ ] Test chat message flow
- [ ] Test AI response parsing
- [ ] Test problem selection
- [ ] Test saving to Firestore
