# Psychology Helper - Project Specification

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

## Core Entities
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

## Feature Priority
1. AUTH - Required first
2. DASHBOARD - Main entry point
3. PROBLEM_INVESTIGATION - Core feature
4. PROBLEM_TRACKING - Track identified problems
5. PROFILE - User settings
6. DAILY_CHECKIN - Engagement feature
