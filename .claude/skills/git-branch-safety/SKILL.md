---
name: git-branch-safety
description: Verify the current git branch before committing or pushing in this repo. Use before any git commit/push, and always before opening or updating a PR — never commit or push directly to main.
---

# Git branch safety

This repo ships via PRs into `main` (see `ci.yml`/`distribute.yml`). Committing or pushing directly to
`main` skips review and can trigger `distribute.yml`'s release/App-Distribution push unreviewed.

Before running `git commit` or `git push`:

1. Run `git branch --show-current`. If it prints `main`, **stop** — create/switch to a feature branch
   first (`git checkout -b <name>`) before committing.
2. This check matters most right after a PR merges — merging a branch on GitHub doesn't change the
   local checkout, but a local `git checkout main` (yours or a prior step's) does, and it's easy to keep
   working there out of habit.
3. If work already landed as a commit on local `main` before this was caught: don't force-push or reset
   `main`. Use `git revert <sha>` (adds a safe undo commit) and push that, then recreate the change on a
   fresh branch (`git checkout -b <name>`, reapply, commit, push, open a PR).

Only push directly to `main` if the user explicitly asks for it in that exact turn.
