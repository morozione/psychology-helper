# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

"MindHelper" (`com.morozione.psychologyhelper`) — a Kotlin Multiplatform / Compose Multiplatform mental-wellness
app targeting **Android** and **iOS**. Features: email/password auth, daily mood tracking, journaling, an
AI chat assistant (Gemini) with mood-aware context, breathing exercises, and a profile/insights dashboard.
Backend is Firebase (Auth, Firestore, Storage) via the GitLive KMP wrappers.

## Build & run

Requires JDK 17+. `gradlew`/`gradlew.bat` need `JAVA_HOME` set — if it isn't, point it at Android Studio's
bundled JBR (e.g. `C:\Program Files\Android\Android Studio\jbr` on Windows).

```bash
# Fast Kotlin-only compile check (no resource/manifest/dex processing) — use this while iterating on errors
./gradlew :composeApp:compileDebugKotlinAndroid

# Full debug APK
./gradlew :composeApp:assembleDebug

# Release APK (unsigned — no release signingConfig is set up yet)
./gradlew :composeApp:assembleRelease

# See errors across all modules instead of stopping at the first failure
./gradlew :composeApp:compileDebugKotlinAndroid --continue
```

There is no test suite in this repo yet (`commonTest` only wires `kotlin-test` in `composeApp`, with no
test sources).

**`composeApp/google-services.json` is required** even just to compile (the `processDebugGoogleServices`
task runs ahead of Kotlin compilation) and is gitignored. Locally, use a real file downloaded from the
Firebase console for `com.morozione.psychologyhelper`. CI (`.github/workflows/ci.yml`,
`distribute.yml`) writes it from the `GOOGLE_SERVICES_JSON` secret, falling back to a placeholder JSON
that's enough to compile but not to exercise real Firebase calls.

**Gemini chat** needs a `GEMINI_API_KEY` Gradle property (`-PGEMINI_API_KEY=...` or in
`gradle.properties`, not committed) — it's wired into `BuildConfig` on Android only.
`shared/core/data/.../GeminiApiKey.ios.kt` currently returns `""`; iOS chat is stubbed, not implemented.

## Module architecture

Gradle modules (see `settings.gradle.kts`):

```
composeApp                    — app entry points (MainActivity/MainViewController), Koin startup, navigation root
shared/core/domain            — entities, repository interfaces, use cases, expect/actual platform utils. No deps on other shared modules.
shared/core/data              — repository impls (Firebase/Ktor), DTOs, Koin `dataModule`/`domainModule`. Depends on domain.
shared/core/ui                — shared Compose components/theme (PsychologyButton, PsychologyCard, charts...). Depends on domain.
shared/feature/{auth,home,mood,journal,profile,chat} — one Gradle module per feature. Depend on domain + ui.
```

`composeApp` depends on every module directly (comment in its `build.gradle.kts` explains why: it needs
direct access to each feature's Koin module to wire up `startKoin`/`KoinApplication`). `shared/feature/home`
also depends on `mood`, `journal`, `profile`, and `chat` project-to-project, since its bottom-nav tabs embed
those features' screen content directly. `shared/feature/chat` additionally depends on `core:data`.

Platform-specific code uses Kotlin's `expect`/`actual` (see `AppPreferences`, `ImagePicker` in
`shared/core/domain/src/{androidMain,iosMain}`, `GeminiApiKey` in `shared/core/data`). **Anything shared
between `composeApp` and a `shared/feature/*` module must live in a `shared/*` module** — `composeApp`
depends on the feature modules, never the other way around, so an expect/actual class defined in
`composeApp` (as `AppPreferences` used to be) is invisible to feature code.

## Per-feature pattern (MVI + Voyager + Koin)

Every `shared/feature/*` module follows the same three-file shape (there's no shared base class — it's a
convention, not inheritance):

- `<Feature>Screen.kt` — Voyager `Screen` (or a `@Composable fun Screen.<X>Content()` extension called from
  another screen, e.g. from `HomeScreen`'s bottom-nav tabs) rendering UI from `state`.
- `<Feature>ScreenModel.kt` — a `data class <Feature>State`, a `sealed class <Feature>Intent`, a
  `sealed class <Feature>Effect`, and a `class <Feature>ScreenModel : ScreenModel` holding
  `MutableStateFlow<State>` + `MutableSharedFlow<Effect>` (one-shot events), with a single
  `onIntent(intent)` entry point and a private `reduce { copy(...) }` helper.
- `<Feature>Module.kt` — a Koin `val xModule = module { factory { XScreenModel(get(), get()) } }`,
  registered centrally in `PsychologyApplication` (Android) / `MainViewController` (iOS).

**`koinScreenModel<T>()`** (from `cafe.adriel.voyager.koin`) is an **extension function on `Screen`**, not
a free function. It only resolves when called either directly inside a `Screen.Content()` override, or
inside a `@Composable fun Screen.SomeContent()` extension. A plain top-level `@Composable fun SomeContent()`
that calls it will fail to compile with a misleading cascade of `Unresolved reference` errors on every
property of `state` (the whole `val state by screenModel.state.collectAsState()` line silently becomes an
error type) — fix by adding `Screen.` as the receiver, not by chasing the individual property errors.

Navigation is Voyager (`cafe.adriel.voyager:voyager-navigator`/`voyager-screenmodel`/`voyager-koin`).
`App.kt` picks the root screen (`OnboardingScreen` → `LoginScreen` → `HomeScreen`) based on
`AuthRepository.currentUser` and `AppPreferences.isOnboardingComplete()`.

## Dependency versions

Versions live in `gradle/libs.versions.toml`. Two upstream libraries in this project have published
inconsistent/incomplete releases before, so when bumping them, verify the new version actually resolves
rather than trusting the version number alone:

- **`dev.gitlive:firebase-*` (`firebase-gitlive`)** — GitLive occasionally tags `firebase-auth`/
  `firebase-firestore`/`firebase-storage`/`firebase-common` at a version whose shared base dependency
  `dev.gitlive:firebase-app` was never published (e.g. 2.6.0 exists for the leaf artifacts but not for
  `firebase-app`, which jumps 2.5.0 → 3.0.0-alpha01). This fails with
  `Could not find dev.gitlive:firebase-app:<version>` at dependency-resolution time.
- **`cafe.adriel.voyager` (`voyager`)** — `koinScreenModel` (vs. the deprecated `getScreenModel`) only
  exists from `1.1.0-alpha04` onward; this repo is pinned past that (`voyager` version tracks a
  `<kotlin>-<compose-multiplatform>`-suffixed release train, e.g. `2.2.21-1.10.3`).

`kotlinx.datetime.Clock`/`kotlinx.datetime.Instant` are deprecated typealiases for `kotlin.time.Clock`/
`kotlin.time.Instant` at the pinned `kotlinx-datetime` version — import from `kotlin.time.Clock`
(`kotlinx.datetime.Clock` no longer resolves at all, unlike `Instant` which still works with a warning).

AGP 9.2.1 warns on every Android library module that `org.jetbrains.kotlin.multiplatform` +
`com.android.library` is deprecated in favor of `com.android.kotlin.multiplatform.library` — not yet
migrated in this repo; currently a warning, not a build failure.

## CI/CD

- `.github/workflows/ci.yml` — builds `:composeApp:assembleDebug` on push/PR to `main`.
- `.github/workflows/distribute.yml` — builds `:composeApp:assembleRelease` and pushes to Firebase App
  Distribution on push to `main` (or manual dispatch). Required secrets are documented at the top of that
  file (`FIREBASE_APP_ID`, `CREDENTIAL_FILE_CONTENT`, `GOOGLE_SERVICES_JSON`, optional
  `FIREBASE_TESTER_GROUPS`).
- Firebase Auth's bot/abuse protection (reCAPTCHA/Play Integrity) is configured per-project in the Firebase
  console, not in code — a `RecaptchaAction(...) CONFIGURATION_NOT_FOUND` error on sign-in/sign-up means
  that protection needs enabling (and/or the signing certificate's SHA-1/SHA-256 fingerprints need to be
  registered) in the Firebase console for the app, not a code fix.

## App versioning

`VERSIONS.md` at the repo root is the single source of truth for the app's version — never hardcode
`versionCode`/`versionName` in `composeApp/build.gradle.kts`. Its format is exactly two lines per
entry (`<code> <title>` / `<description>`), newest entry at the top, one blank line between entries:

- **`versionCode`** and **`versionName`** are both read at build time (locally and in CI, identically)
  from the topmost entry — `<code>` (an integer that must equal the total number of entries in the
  file) becomes `versionCode`, `<title>` becomes `versionName`. The Gradle build fails fast if `<code>`
  doesn't match the entry count.
- Add a new entry at the top before opening a PR that should ship a new build — see the
  `release-version` skill. Bumping the version is a `VERSIONS.md` edit, never a Gradle or CI edit.
- The newest entry's one-line `<description>` is used verbatim as the Firebase App Distribution release
  notes in `distribute.yml` (overridable via the `release_notes` manual-dispatch input; `distribute.yml`
  also re-validates `<code>` against the entry count before building, failing fast on a bad bump).
