# ADR 0002: Keep the validated target SDK and report freshness as information

Date: 2026-11-16
Status: Accepted for the foundation CI correction.

## Context

Cheeky Van App is a personal application tested on Android 16 / API 36. Compile and
target SDK are pinned to 36. AGP 8.13 supports up to API 36.1, as documented in its
[compatibility table](https://developer.android.com/build/releases/agp-8-13-0-release-notes).
Changing target SDK changes Android compatibility behavior and requires a reviewed
toolchain migration and device testing.

Hosted CI run `43958929461` failed solely on `OldTargetApi`, a latest-target advisory
promoted from warning to error by `warningsAsErrors`. Its result depends on SDK
freshness information available to the lint environment.

## Decision

Keep compile/target API 36 and mark only `OldTargetApi` as informational using the
[AGP lint DSL](https://developer.android.com/reference/tools/gradle-api/8.13/com/android/build/api/dsl/Lint#informational).
The advisory remains in lint reports when detected. All other enabled lint warnings
still fail validation; `abortOnError` and `warningsAsErrors` stay enabled.

## Consequences

CI enforces the validated foundation while retaining the SDK migration reminder.
Revisit this policy when changing the owner's Android version, adopting a newer
toolchain or preparing store distribution. Any target increase requires Android
behavior-change review and applicable device tests before approval.
