# Psychology Helper - Claude Instructions

## Project Overview
A Kotlin Multiplatform mobile app for Android and iOS that helps users identify, track, and solve psychological problems through AI-guided conversations.

## Documentation-Driven Development
This project follows a documentation-first approach:

1. **Read specs first**: Before implementing any feature, read the relevant doc in `docs/features/`
2. **Update docs**: When requirements change, update the docs first
3. **Track status**: Update feature status in docs as work progresses

## Documentation Structure
```
docs/
├── SPEC.md                 # Project overview and architecture
└── features/
    ├── AUTH.md             # Authentication (Google Sign-In)
    ├── PROFILE.md          # User profile
    ├── DASHBOARD.md        # Main dashboard screen
    ├── PROBLEM_INVESTIGATION.md  # AI chat for problem identification
    ├── PROBLEM_TRACKING.md       # Problem list and daily hints
    └── DAILY_CHECKIN.md          # Daily check-in questions
```

## Tech Stack
- **Language**: Kotlin
- **Framework**: Kotlin Multiplatform (KMP)
- **UI**: Compose Multiplatform
- **Backend**: Firebase (Auth, Firestore)
- **AI**: Gemini 2.5 API

## Code Structure
```
composeApp/
├── src/
│   ├── commonMain/         # Shared code (business logic, UI)
│   │   ├── kotlin/
│   │   │   ├── data/       # Repositories, data sources
│   │   │   ├── domain/     # Use cases, models
│   │   │   ├── ui/         # Compose screens, components
│   │   │   └── di/         # Dependency injection
│   ├── androidMain/        # Android-specific code
│   └── iosMain/            # iOS-specific code
iosApp/                     # iOS app entry point
```

## Secrets & API Keys
**Never commit these files:**
- `google-services.json` - Firebase config (Android)
- `GoogleService-Info.plist` - Firebase config (iOS)
- `local.properties` - Contains `GEMINI_API_KEY`

## Implementation Guidelines

### When implementing a feature:
1. Read the feature doc in `docs/features/`
2. Check user stories and acceptance criteria
3. Follow the data models defined in the doc
4. Update status checkboxes as you complete items
5. Mark feature status as `in-progress` or `done`

### Code style:
- Use Kotlin coroutines for async operations
- Follow repository pattern for data access
- Use StateFlow for UI state management
- Keep Compose components small and focused

### Firebase conventions:
- Collection: `users/{uid}/...`
- Use Firestore snapshots for real-time updates
- Handle offline persistence

### Gemini API conventions:
- System prompts defined in feature docs
- Parse structured responses (SUMMARY:, PROBLEMS:, etc.)
- Handle rate limits gracefully

## Feature Priority
1. AUTH - Required first
2. DASHBOARD - Main entry point
3. PROBLEM_INVESTIGATION - Core feature
4. PROBLEM_TRACKING - Track identified problems
5. PROFILE - User settings
6. DAILY_CHECKIN - Engagement feature

## Commands
```bash
# Run Android app
./gradlew :composeApp:assembleDebug

# Run iOS app (from iosApp folder)
xcodebuild -workspace iosApp.xcworkspace -scheme iosApp

# Run tests
./gradlew test
```
