# Psychology Helper - Claude Instructions

## Project Overview
A Kotlin Multiplatform mobile app for Android and iOS that helps users identify, track, and solve psychological problems through AI-guided conversations.

## OpenSpec - Spec-Driven Development
This project uses **OpenSpec** for spec-driven development.

### Workflow
```
/opsx:propose "feature-name"  → Create proposal, design, tasks
/opsx:apply                   → Implement the tasks
/opsx:archive                 → Archive completed change
```

### Structure
```
openspec/
├── changes/          # Active changes being worked on
│   └── feature-name/
│       ├── proposal.md   # What & why
│       ├── design.md     # How (technical approach)
│       └── tasks.md      # Implementation checklist
└── specs/            # Baseline specifications
```

### Before implementing:
1. Run `/opsx:propose` to create change artifacts
2. Review generated proposal, design, and tasks
3. Run `/opsx:apply` to implement
4. Run `/opsx:archive` when done

## Baseline Specs
Reference documentation in `docs/` contains initial feature specs:
- `docs/SPEC.md` - Project overview and architecture
- `docs/features/` - Feature-level specs (AUTH, DASHBOARD, etc.)

These serve as baseline context for OpenSpec proposals.

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

## Code Style
- Use Kotlin coroutines for async operations
- Follow repository pattern for data access
- Use StateFlow for UI state management
- Keep Compose components small and focused

## Firebase Conventions
- Collection: `users/{uid}/...`
- Use Firestore snapshots for real-time updates
- Handle offline persistence

## Gemini API Conventions
- System prompts defined in specs
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

# OpenSpec
openspec status              # Check current change status
openspec update              # Update OpenSpec commands
```
