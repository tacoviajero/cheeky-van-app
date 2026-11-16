# Foundation validation evidence

Date: 2026-11-16. Scope: milestone 0, development version 0.1.0.

## Automated results

The final full Gradle gate succeeded in 1m 31s (90 tasks: 17 executed, 73 up-to-date).

| Check | Result |
| --- | --- |
| Official Gradle distribution and wrapper SHA-256 | Passed |
| Kotlin and Gradle formatting | Passed |
| Strict Android lint | Passed |
| JVM privacy tests | 3 tests passed; 0 failures, errors or skipped tests |
| Debug application and instrumentation APK builds | Passed |
| Packaged-manifest privacy check | Passed |
| Invalid manifest mutations | INTERNET permission, enabled backup and weak custom permission rejected |
| Workflow static validation | Passed with checksum-verified actionlint 1.7.12 |
| PowerShell helper syntax | Passed |
| Connected Android devices | None attached; ADB starts successfully outside the restricted sandbox |

Final Gradle command, with project-local toolchain configured:

```powershell
. .\scripts\Use-LocalToolchain.ps1
.\gradlew.bat spotlessApply spotlessCheck :app:lintDebug :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest --no-daemon --console=plain
.\scripts\Test-PackagedManifest.ps1
```

Report locations: `app/build/reports/tests/testDebugUnitTest/index.html`,
`app/build/reports/lint-results-debug.html`, and `app/build/test-results/testDebugUnitTest/`.
APK locations: `app/build/outputs/apk/debug/app-debug.apk` and
`app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk`.

## Repository and hosted CI

Local identity: Juan Nunez, using the email provided by the owner. Git has an empty
initial `main` commit and the working `feature/milestone-0-foundation` branch.
`origin` is `https://github.com/tacoviajero/cheeky-van-app.git`. The remote was verified
empty. CLI push failed because terminal Git has no credentials. The connected GitHub
integration has write access, but automatic approval review rejected creating the
initial `.gitignore` directly on `main`: the authorized workflow targets a feature
branch and PR. Owner approval of this initialization is pending. No remote write
succeeded; no PR exists, hosted CI has not run, and branch protection is unconfigured.

## Remaining acceptance gates

- Execute the Compose launch instrumentation test on a device/emulator.
- Inspect portrait/landscape, large fonts, safe areas and scrolling on Android.
- Approve remote initialization, publish the feature branch, open a PR and verify hosted CI.
- Review the changes and configure required checks/branch protection before merging.
- Confirm package identifier ownership before a signed distribution.

No hardware test, vehicle connection, refresh-rate measurement or release validation
has been performed. A native symbol-stripping notice for the upstream Compose graphics
library was informational; the library is packaged intact in the development APK.

Milestone 0 remains under review until these gates are satisfied. The recommended next
implementation is milestone 1 (simulation), after owner approval of the foundation.
