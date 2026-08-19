---
name: new-feature-module
description: Scaffold a new shared/feature/<name> Kotlin Multiplatform module following this repo's MVI + Voyager + Koin convention (Screen, ScreenModel, Module files, Gradle wiring, Koin registration). Use when adding a brand-new feature area to the app, not when editing an existing feature.
---

# New feature module scaffold

This repo has no shared base class for features — every `shared/feature/*` module follows the same
three-file convention by hand (see CLAUDE.md's "Per-feature pattern" section). To add a new feature
`<name>`:

1. **Gradle module**: create `shared/feature/<name>/build.gradle.kts` mirroring an existing simple feature
   module (e.g. `shared/feature/mood`) — same plugins, depends on `shared/core/domain` and
   `shared/core/ui` (add `shared/core/data` too only if it needs repositories directly, like
   `shared/feature/chat` does).
2. Add `include(":shared:feature:<name>")` to `settings.gradle.kts`.
3. **Three files** under `shared/feature/<name>/src/commonMain/kotlin/.../<name>/`:
   - `<Name>Screen.kt` — a Voyager `Screen` (or a `@Composable fun Screen.<Name>Content()` extension if
     it's embedded inside another screen, the way `HomeScreen`'s bottom-nav tabs embed `mood`/`journal`/
     `profile`/`chat`) that renders UI from `state`.
   - `<Name>ScreenModel.kt` — `data class <Name>State`, `sealed class <Name>Intent`,
     `sealed class <Name>Effect`, and `class <Name>ScreenModel : ScreenModel` holding a
     `MutableStateFlow<State>` and a `MutableSharedFlow<Effect>` (one-shot events), with a single
     `onIntent(intent)` entry point and a private `reduce { copy(...) }` helper.
   - `<Name>Module.kt` — `val <name>Module = module { factory { <Name>ScreenModel(get(), ...) } }`.
4. **Register the Koin module** in `PsychologyApplication` (Android) and `MainViewController` (iOS) in
   `composeApp`, alongside the other feature modules' entries.
5. **Depend on it from `composeApp`** (`implementation(project(":shared:feature:<name>"))`) —
   `composeApp` depends on every feature module directly (so it can reach each one's Koin module at
   startup); never make a feature module depend back on `composeApp`.
6. If it needs to embed an existing feature's screen content (like `home` does), add a
   project-to-project dependency on that feature module directly, not through `composeApp`.

`koinScreenModel<T>()` only resolves inside a `Screen.Content()` override or a
`@Composable fun Screen.<X>Content()` extension — see the `kmp-compile-check` skill if step 3 fails to
compile with a confusing cascade of errors. Run that skill's fast compile check before building the full
APK.
