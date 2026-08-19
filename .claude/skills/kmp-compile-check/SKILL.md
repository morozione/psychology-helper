---
name: kmp-compile-check
description: Fast iteration loop for fixing Kotlin Multiplatform compile errors in this repo, including the koinScreenModel() receiver gotcha that produces misleading cascading errors. Use whenever Kotlin code in composeApp or shared/* fails to compile.
---

# KMP compile-check loop

Use the fast Kotlin-only check while iterating on errors — it skips resource/manifest/dex processing:

```bash
./gradlew :composeApp:compileDebugKotlinAndroid
```

To see every error across all modules instead of stopping at the first one:

```bash
./gradlew :composeApp:compileDebugKotlinAndroid --continue
```

Only run a full `:composeApp:assembleDebug` once the fast check is clean — it's much slower and also
requires `composeApp/google-services.json` to exist locally (gitignored; see CLAUDE.md for how to get one).

## Common trap: `koinScreenModel<T>()` misresolution

`koinScreenModel<T>()` (`cafe.adriel.voyager.koin`) is an **extension function on `Screen`**, not a free
function. Calling it from a plain top-level `@Composable fun SomeContent()` fails to compile — but the
compiler reports it as `Unresolved reference` on *every single property* of `state`, which looks like the
state class itself is broken (`val state by screenModel.state.collectAsState()` silently becomes an error
type). It isn't. Fix by changing the function signature to `@Composable fun Screen.SomeContent()` — adding
the `Screen.` receiver — not by chasing the individual property errors one by one.

## Other version gotchas (full detail in CLAUDE.md)

- Import `Clock`/`Instant` from `kotlin.time`, not `kotlinx.datetime` — `kotlinx.datetime.Clock` no longer
  resolves at all at the pinned `kotlinx-datetime` version (unlike `Instant`, which still resolves with a
  deprecation warning).
- If bumping `dev.gitlive:firebase-*` or `cafe.adriel.voyager` in `gradle/libs.versions.toml`, verify the
  new version actually resolves before trusting the version number — both have shipped inconsistent
  releases before (see CLAUDE.md's "Dependency versions" section for the specific failure modes).
