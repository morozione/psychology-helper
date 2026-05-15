# Proposal: Google Sign-In Authentication

## What
Implement user authentication using Google Sign-In via Firebase Authentication.

## Why
- Users need a secure, familiar way to access the app
- Google Sign-In provides seamless onboarding with minimal friction
- Firebase Auth handles token management and session persistence
- No need for email/password management complexity

## Scope
- Welcome/Login screen with Google Sign-In button
- Sign-out functionality
- Session persistence between app launches
- User document creation in Firestore on first sign-in

## Out of Scope
- Email/password authentication
- Other social providers (Apple, Facebook)
- Account linking
- Password reset flows

## Success Criteria
- User can sign in with Google account
- User can sign out
- User remains signed in between app sessions
- User document is created in Firestore on first sign-in
