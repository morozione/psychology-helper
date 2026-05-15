# Tasks: Google Sign-In Authentication

## Setup
- [ ] Add Firebase Auth dependencies to build.gradle.kts
- [ ] Add Google Sign-In dependencies (Android/iOS)
- [ ] Configure google-services.json (Android)
- [ ] Configure GoogleService-Info.plist (iOS)

## Data Layer
- [ ] Create `AuthUser` data class in commonMain
- [ ] Create `AuthRepository` interface in commonMain
- [ ] Implement `AuthRepositoryImpl` for Android
- [ ] Implement `AuthRepositoryImpl` for iOS
- [ ] Create user document in Firestore on first sign-in

## Domain Layer
- [ ] Create `SignInWithGoogleUseCase`
- [ ] Create `SignOutUseCase`
- [ ] Create `GetCurrentUserUseCase`

## UI Layer
- [ ] Create `AuthScreen` composable (Welcome screen)
- [ ] Create `AuthViewModel` with auth state
- [ ] Add Google Sign-In button component
- [ ] Handle loading and error states
- [ ] Navigate to Dashboard on successful sign-in

## Navigation
- [ ] Set up auth navigation graph
- [ ] Add auth state check on app launch
- [ ] Redirect unauthenticated users to AuthScreen

## Testing
- [ ] Test sign-in flow on Android
- [ ] Test sign-in flow on iOS
- [ ] Test session persistence
- [ ] Test sign-out flow
