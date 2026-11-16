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
| Connected Android device | Redmi Note 14 Pro+ 5G authorized in ADB; Android 16 / API 36 |
| Device launch instrumentation | Renamed APK passed: `OK (1 test)` in 1.578s; earlier APK passed in 2.425s |

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

Local identity: Juan Nunez, using the email provided by the owner. The working branch
is `feature/milestone-0-foundation`; foundation source commit: `8d89c5c`.
`origin` is `https://github.com/tacoviajero/cheeky-van-app.git`.

After explicit owner approval, the base `.gitignore` commit
`66392f4ef881a7176260c0e2319bffe1e02b49e2` was published and verified on remote `main`.
Terminal Git lacked push credentials; publication used the connected GitHub integration.
On 2026-11-16 the version-control chat reconciled the unpublished branch onto remote
`main`, preserving the source tree and a local backup reference. Original foundation
`8d89c5c` became `78468d3`; the name/scope correction is `a8f0447` and project/device
documentation is `f20a6ac`. Terminal authentication was subsequently configured and
the feature branch was published through Git.

PR: https://github.com/tacoviajero/cheeky-van-app/pull/1 (base `main`).
The initial PR workflow run `43957968079` failed during SDK setup because the action's
default package list includes the retired `tools` package. CI now explicitly selects
`platform-tools`; Android platform 36 and build tools 35.0.0 remain pinned below it.
Dependency review also failed because Dependency graph was disabled. The owner enabled
it and the subsequent PR run passed dependency review.

Hosted PR run: https://github.com/tacoviajero/cheeky-van-app/actions/runs/43958248302
for `a1183c3`. SDK setup, wrapper validation, dependency resolution and formatting passed.
`Validate Android` failed at strict lint with `OldTargetApi` in `app/build.gradle.kts:14`:
the hosted lint check considers target API 36 outdated. Packaged-manifest privacy and
APK upload steps were skipped after the lint failure. This hosted run does not establish
successful JVM tests or both APK builds. The prior local and device results above remain
separate evidence.

The development chat subsequently supplied the targeted lint-policy correction described
below. Hosted run `43962044697` verified the correction successfully on `17754f7`.
The earlier failing runs remain historical evidence.

Repository API inspection confirmed `main` is unprotected and the rulesets list is
empty. Owner configuration and approval remain required before merge.

## Android device session — results

On 2026-11-16, ADB started successfully outside the restricted sandbox and
`adb devices -l` returned no attached devices. After the owner connected the phone,
Windows detected `Redmi Note 14 Pro+ 5G` as a portable USB device without a reported
device error; ADB still returned no attached devices. USB debugging/authorization
was subsequently enabled/authorized. On retry, ADB recognized the phone and reported
Android 16 / API 36. The existing application APK initially received
`INSTALL_FAILED_USER_RESTRICTED: Install canceled by user` during streamed installation.
After the owner enabled USB installation and accepted the prompt, installation using
`adb install --no-streaming -r` succeeded for both existing debug APKs.

`FoundationLaunchTest` then passed on the physical phone: `OK (1 test)`, duration
2.425s. It verified the displayed title and connection status and the presence of
the safety notice. A subsequent explicit cold launch returned `Status: ok` and
`TotalTime: 1000` ms; the app was left open for owner visual review. That single
observed launch time is not a performance guarantee. Visual checks remain pending.

APK-chat handoff: prepare application and instrumentation debug APKs from foundation
source `8d89c5c`, recording any later source changes and the exact revision used.
Application ID: `com.machinedashboard`, version `0.1.0`, minimum API 23.
Instrumentation ID: `com.machinedashboard.test`; runner:
`androidx.test.runner.AndroidJUnitRunner`. Install both APKs for automated device testing.
APK generation/export is owned by that chat; this chat prepares and evaluates the tests.

After installation and USB authorization, the development test can run without invoking
Gradle packaging tasks:

```powershell
adb shell am instrument -w -r -e class com.machinedashboard.FoundationLaunchTest com.machinedashboard.test/androidx.test.runner.AndroidJUnitRunner
```

Observed automated result: one successful launch test. Device acceptance also requires
the remaining visual checks below.

| Device check | Expected result | Actual result |
| --- | --- | --- |
| Launch | Title, unavailable-connection status and safety notice accessible | Renamed APK passed the launch test; title verified in device captures |
| Portrait and landscape | Readable content; scrolling exposes all text | Passed at 1220×2712 and 2712×1220; all current content fits |
| Font scale 200% | Text and safety notice remain accessible | Passed in both orientations; all current content fits without scrolling |
| System bars and cutout | Content stays inside safe areas | Passed visual inspection of four device captures |
| Background and return | Screen returns without a crash | Passed: launcher opened, app resumed with `Status: ok` |
| Permissions | Foundation opens without sensitive permission prompts | Passed in the inspected launch; packaged manifest privacy check also passed |
| Instrumentation | `FoundationLaunchTest` passes on the recorded device/API | Renamed APK passed: 1 test, 1.578s, Android 16 / API 36 |

## Remaining acceptance gates

- Owner review of the foundation; scrolling beyond the current content and additional device sizes remain future regression cases.
- Hosted CI passed on correction `17754f7`; verify the latest documentation revision in PR checks before merge.
- Review the changes and configure required checks/branch protection before merging.
- Confirm package identifier ownership before a signed distribution.

Vehicle/ELM327 hardware tests, refresh-rate measurements and release validation remain
pending. A native symbol-stripping notice for the upstream Compose graphics
library was informational; the library is packaged intact in the development APK.

Milestone 0 remains under review until these gates are satisfied. The recommended next
implementation is milestone 1 (simulation), after owner approval of the foundation.

## Product name and scope correction — 2026-11-16

The owner confirmed the product name as **Cheeky Van App**, a personal Android app
optimized for our VW Transporter T5, 2.0 TDI CAAC, manual. The app label, screen title,
Gradle project name and theme now use Cheeky Van App. Project and architecture notes
prioritize this vehicle; multivehicle support is outside the approved scope.

The initial device result applied to the previously installed APK. The renamed APK
has since been exported, installed and checked as recorded below.
The existing internal Android package ID remains `com.machinedashboard` for continuity
with the installed development app. This internal identifier is separate from its label.
Source validation passed offline: `spotlessCheck`, `processDebugResources`,
`compileDebugKotlin`, `testDebugUnitTest` (3 passed), and `lintDebug`.
Gradle completed successfully in 1m 17s. The packaged-manifest privacy check also passed.

## Renamed APK export — 2026-11-16

The APK chat exported debug application and instrumentation APKs from
`8d89c5cfdfbf98194ead8361176cc1ca32d9de80` plus the local name and documentation
changes listed in the delivery manifest. The offline Gradle gate passed in 46s
(87 actionable tasks: 8 executed, 79 up-to-date): `spotlessCheck`, `lintDebug`,
`testDebugUnitTest`, `assembleDebug`, and `assembleDebugAndroidTest`.
JVM tests were up-to-date; their report records 3 passed, zero failures/errors/skips.
The packaged-manifest privacy check passed. AAPT verified the packaged application
label as `Cheeky Van App`, package `com.machinedashboard`, version `0.1.0` (1),
minimum API 23 and target API 36. Both exported APK signatures verified (v1/v2).
Instrumentation targets `com.machinedashboard` using
`com.machinedashboard.test/androidx.test.runner.AndroidJUnitRunner`.

Delivery directory: `app/build/exports/2026-11-16-8d89c5c-renamed-debug/`.
`delivery.json` records source hashes and checks; `SHA256SUMS.txt` records APK hashes.

- `CheekyVanApp-0.1.0-debug.apk`: SHA-256 `40056100828A0A6FE6C0663B807C08326DDD43F01A3A0E16EA2C1C2D7FD8EDA5`.
- `CheekyVanApp-0.1.0-debug-androidTest.apk`: SHA-256 `A728F06A8674F4BF55BCE0AB529B8DC8B46B35217E63C2635DAA709C157BDC73`.

## Renamed APK device verification — 2026-11-16

The development chat verified both exported APK SHA-256 hashes against the delivery
record and installed them using `adb install --no-streaming -r` on the Redmi Note
14 Pro+ 5G, Android 16 / API 36.

Two initial instrumentation attempts failed with `No compose hierarchies found`.
Device diagnostics then showed `mWakefulness=Dozing` and keyguard showing. After the
owner unlocked the phone, the same installed APKs passed `FoundationLaunchTest`:
`OK (1 test)` in 1.578s. This records the failed attempts and the successful retry;
the screen-lock condition is an instrumentation prerequisite for future sessions.

Visual inspection of private app captures confirmed the title **Cheeky Van App**,
connection status, description and full safety notice at font scales 1.0 and 2.0,
in portrait (1220×2712) and landscape (2712×1220). Content remained clear of system
bars. All current text fit the viewport in these four cases; an actual scroll gesture
was unnecessary and was not validated. Narrower windows remain a future regression case.

An initial large-text capture was interrupted by unsettled orientation and later
screen locking. The completed large-text pass waited for the requested display
orientation before capture and kept the phone awake over USB. Opening the launcher
and returning to the app succeeded. The completed pass restored its original font
scale 1.0, portrait lock (`lock 0`), and stay-awake setting 0.

Private screenshots, UI hierarchy dumps and local check results are under ignored
`.tools/device-validation/renamed/`; they are excluded from Git and CI artifacts.
ADB input-event injection was denied by the phone during diagnosis; no input-injection
permission was enabled. The completed checks used activity launches and temporary
display settings instead.

The initial handoff to the version-control chat was rejected by automatic approval
review. The owner subsequently explicitly authorized the handoff, commits, feature
branch publication, PR creation and CI verification on 2026-11-16. The version-control
chat accepted that work; publication and hosted results are recorded in the repository
section as they become available. Merge remains subject to explicit owner approval.

## OldTargetApi CI correction — 2026-11-16

The development chat inspected hosted run
https://github.com/tacoviajero/cheeky-van-app/actions/runs/43958929461 and the
`Validate Android` job log (`133620815048`). Dependency review passed; Android lint
failed with exactly one issue, `OldTargetApi`, at target SDK 36.

The correction retains compile/target API 36 and sets only `OldTargetApi` to
`informational` in the AGP lint DSL. The advisory remains visible as `Hint` in lint
reports when triggered. `warningsAsErrors` and `abortOnError` remain enabled.
Rationale and migration conditions are recorded in
[ADR 0002](decisions/0002-target-sdk-lint-policy.md).

Local gate passed offline in 1m 37s (50 tasks: 12 executed, 38 up-to-date):
`spotlessCheck`, `lintDebug`, `compileDebugKotlin`, `compileDebugAndroidTestKotlin`,
and `testDebugUnitTest`. JVM tests were up-to-date; their report records 3 passed,
zero failures/errors/skips. The packaged-manifest privacy check passed separately.

An isolated copy under ignored `.tools/lint-policy-probe/` used temporary target API
35 to trigger the same advisory with the locally installed SDK 36. Before the policy
change, lint failed with one `OldTargetApi` error. With the correction, lint passed
in 1m 14s and the XML/text reports retained one `OldTargetApi` hint (zero errors or
warnings). The real project's compile/target SDK stayed at 36 throughout.

A negative check removed the application icon only in that isolated copy. Lint
failed as expected in 1m 1s: `MissingApplicationIcon` and `UnusedResources` were
errors, while `OldTargetApi` remained a hint. The probe manifest was then restored.
The original/error, corrected/hint and unrelated-warning XML evidence is under
ignored `.tools/lint-policy-*.xml`; these fixtures are excluded from Git and CI.
The version-control chat published the correction as
`17754f7fda320eed846835cc0dc2170c4142ffdb` on the existing PR branch.
Hosted PR run https://github.com/tacoviajero/cheeky-van-app/actions/runs/43962044697
completed successfully: both `Validate Android` and `Review dependency vulnerabilities`
passed. Wrapper validation, formatting, strict lint with the documented advisory policy,
JVM tests, both debug APK builds and packaged-manifest privacy all passed. Validation
reports and debug APK artifacts were uploaded successfully by CI. This run does not
execute connected-device instrumentation; the recorded physical-device checks remain
separate evidence. The subsequent evidence-only commit triggers fresh PR checks.

Owner review and main branch-protection configuration remain pending. Merge requires
the owner's explicit approval; milestone 1 remains subject to approval.
