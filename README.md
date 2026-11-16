# Cheeky Van App

Personal native Android telemetry and diagnostics application, designed and optimized
for our 2013 Volkswagen Transporter T5, 2.0 TDI CAAC, manual, with a Bluetooth Classic
ELM327-compatible adapter. Our van and its owners' needs guide the interface, telemetry
and performance decisions. Version: **0.1.0**, foundation under review.

The current application opens a dark, scrollable foundation screen with connection
availability and a safety notice. Telemetry and adapter connectivity are planned in
the following milestones. Always use the vehicle's instruments and manufacturer warnings.

## Requirements and setup

- Android Studio with support for AGP 8.13.2, or command-line Android SDK tools.
- JDK 17; configure Android Studio's Gradle JDK accordingly.
- Android SDK platform 36 and Build Tools 35.0.0.
- Android 6.0 / API 23 or later on the test device.
- Network access for the first Gradle/dependency download.

Open this directory in Android Studio and let it synchronize. Install the required
SDK packages in SDK Manager and accept their licenses. For CLI builds set `JAVA_HOME`
and `ANDROID_HOME`, or put `sdk.dir=C:/path/to/Android/Sdk` in ignored `local.properties`.
Use `sdkmanager 'platforms;android-36' 'build-tools;35.0.0' 'platform-tools'` with
command-line tools 19.0. The newer Android CLI has different syntax.

Dependencies are fixed in `gradle/libs.versions.toml`. The official Gradle wrapper
includes a distribution SHA-256 checksum. See [toolchain provenance](docs/toolchain.md).
Locally downloaded validation tools and caches reside in ignored `.tools/`.

## Build and tests

Windows PowerShell:

```powershell
# Use this helper only with the project-local tools prepared in this workspace:
. .\scripts\Use-LocalToolchain.ps1
.\gradlew.bat spotlessCheck :app:lintDebug :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest
.\scripts\Test-PackagedManifest.ps1
```

On Linux/macOS use `./gradlew` with the same tasks. Run `spotlessApply` to format
Kotlin and build scripts. Lint and Kotlin compiler warnings fail validation.

With an emulator or authorized USB device connected, run:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
adb devices
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

The APK is debug-signed for development. Release signing must be configured outside
version control. See [testing](docs/testing.md) and [validation results](docs/validation.md).

## Bluetooth and supported telemetry

For future live validation, pair the adapter in Android system settings first.
The app will select from already-paired devices and request connection permission
when needed. The read-only telemetry scope is RPM (`010C`), speed (`010D`), coolant
temperature (`0105`), and calculated engine load (`0104`), after supported-PID discovery.
These operations are scheduled for milestone 2. Availability on this T5 requires
hardware validation; generic OBD-II does not guarantee Volkswagen-specific values.

## Architecture and delivery

One Android module, with separate presentation, domain, data, and transport packages.
See [architecture](docs/architecture.md), [decisions](docs/decisions/0001-foundation.md),
[milestone acceptance](docs/milestone-0.md), and [contribution guide](CONTRIBUTING.md).
GitHub Actions validates formatting, lint, unit tests, and debug APKs; connected-device
tests are a separate required gate until an emulator CI job is configured.

## Privacy and limitations

The foundation requests no sensitive platform permissions and has no network, Bluetooth, location,
telemetry, persistence, or logging feature. Backup and transfer rules exclude app data.
AndroidX contributes one internal signature permission that requires no user prompt.
OEM device-to-device transfer policies can vary. Future preferences and diagnostics
remain local, with explicit read-only command allowlisting and opt-in redacted logs.
Never put keys, identifiers, raw vehicle logs, or private captures in Git or CI artifacts.

Simulation, configurable dashboards, GPS, maps, trips, fuel estimation,
and diagnostic history for our T5 remain in the roadmap. The current scope targets
this single vehicle. The next milestone starts after the
owner approves the foundation. Hardware behavior and UI rendering require device validation.
