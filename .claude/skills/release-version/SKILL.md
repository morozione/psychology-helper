---
name: release-version
description: Bump VERSIONS.md with a new version number and short description before opening a PR that should ship a new app build. Use when finishing a feature/fix branch in this repo and about to create a PR against main.
---

# Release version bump

This repo's app version is tracked entirely in `VERSIONS.md` at the repo root — never hardcode
`versionCode`/`versionName` in `composeApp/build.gradle.kts`. Before opening a PR that should ship a new
build:

1. Decide the next version title (`MAJOR.MINOR.PATCH`):
   - **PATCH** — bug fixes or small tweaks with no user-visible feature change.
   - **MINOR** — new features or visibly different UI/behavior.
   - **MAJOR** — only when the user explicitly asks for it.
2. Add a new entry at the **top** of `VERSIONS.md`, above the previous newest entry, as exactly two
   lines with one blank line before the next entry:

   ```
   <code> <title>
   <description>
   ```

   - `<code>` — an integer, one higher than the previous top entry's code. It must equal the total
     number of entries in the file after adding yours (the Gradle build and CI both fail otherwise) —
     in practice this just means "count the entries in the file, that's the new code."
   - `<title>` — the version number, e.g. `0.2.0`. Becomes `versionName`.
   - `<description>` — one line, no line breaks. Becomes the Firebase App Distribution release notes
     verbatim, so keep it short and user-facing.

   Example, adding a second version on top of the existing `1 0.1.0 / ...` entry:

   ```
   2 0.2.0
   Added guided breathing exercises and a weekly mood summary chart.

   1 0.1.0
   Initial tracked version baseline: ...
   ```

3. Both `versionCode` and `versionName` are read straight from this top entry at build time (locally and
   in CI) — don't edit `composeApp/build.gradle.kts` or any CI workflow to bump a version.
4. Commit `VERSIONS.md` together with the rest of the PR's changes.

Skip this only for changes that don't warrant a new shipped version — docs-only edits, CI-only tweaks,
internal refactors with no behavior change. Ask the user if it's unclear whether a PR should bump the
version.
