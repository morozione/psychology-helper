# Versions

Machine-parsed by `composeApp/build.gradle.kts` and `.github/workflows/distribute.yml` — keep the exact
per-entry shape below, newest entry at the top, one blank line between entries. Each entry is exactly
two lines:

    <code> <title>      code is an integer equal to the total number of entries in this file — i.e.
                         just "count the versions", never reuse or skip a number. title becomes
                         versionName in composeApp/build.gradle.kts.
    <description>        one line, becomes the Firebase App Distribution release notes.

Add a new entry at the top before opening a PR that should ship a new build — see the `release-version`
skill.

4 0.4.0
Switched to a supported Gemini model with retry on temporary overload, plus iOS build support in CI.

3 0.3.0
Signed release builds (fixes App Distribution install failures) and added Sign in with Google.

2 0.2.0
New app icon, fixed the glued-together app name, and CI now auto-adds a default Firebase App Distribution tester.

1 0.1.0
Initial tracked version baseline: email/password auth, daily mood tracking, journaling, an AI chat assistant, breathing exercises, and a profile/insights dashboard.
