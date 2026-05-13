# Psychology Helper - Specification

## Overview
A mobile app that helps users identify, track, and solve their psychological problems through AI-guided conversations and daily check-ins.

## Tech Stack
- **Framework**: Kotlin Multiplatform (Android + iOS)
- **UI**: Compose Multiplatform
- **Backend**: Firebase (Auth, Firestore Database)
- **AI**: Gemini 2.5 API (free tier)
- **Auth**: Google Sign-In via Firebase

## Architecture
```
┌─────────────────────────────────────────────┐
│                    App                       │
├─────────────────────────────────────────────┤
│  UI Layer (Compose Multiplatform)           │
├─────────────────────────────────────────────┤
│  Domain Layer (Use Cases)                   │
├─────────────────────────────────────────────┤
│  Data Layer (Repositories)                  │
├──────────────────┬──────────────────────────┤
│  Firebase        │  Gemini API              │
│  - Auth          │  - Chat                  │
│  - Firestore     │  - Analysis              │
└──────────────────┴──────────────────────────┘
```

## Features Overview
| Feature | Doc | Status |
|---------|-----|--------|
| Authentication | [AUTH.md](features/AUTH.md) | planned |
| Profile | [PROFILE.md](features/PROFILE.md) | planned |
| Dashboard | [DASHBOARD.md](features/DASHBOARD.md) | planned |
| Problem Investigation | [PROBLEM_INVESTIGATION.md](features/PROBLEM_INVESTIGATION.md) | planned |
| Problem Tracking | [PROBLEM_TRACKING.md](features/PROBLEM_TRACKING.md) | planned |
| Daily Check-in | [DAILY_CHECKIN.md](features/DAILY_CHECKIN.md) | planned |

## Data Models
See individual feature docs for detailed models.

### Core Entities
- **User** - Firebase Auth user with profile data
- **Problem** - Identified issue with status and hints
- **ChatSession** - Conversation history with AI
- **DailyCheckIn** - Daily questions and answers

## API Keys Required
| Service | File | Key |
|---------|------|-----|
| Firebase | `google-services.json` (Android) | Downloaded from Firebase Console |
| Firebase | `GoogleService-Info.plist` (iOS) | Downloaded from Firebase Console |
| Gemini | `local.properties` | `GEMINI_API_KEY=xxx` |
