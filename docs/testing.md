# Testing

## Foundation automation

`spotlessCheck` enforces Kotlin formatting via ktlint and project whitespace.
`lintDebug` runs Android static analysis with warnings treated as errors. Kotlin compiler
warnings also fail builds. `testDebugUnitTest` runs three privacy regression tests:
the app requests only paired-Bluetooth connection permissions, disables Android backup with explicit rules,
and excludes persistent storage from both cloud backup and device transfers. These parse
the actual source manifest and backup configuration, rather than generated constants.
Dependency-version update notices are excluded from lint because the compatible
toolchain is deliberately pinned; Dependabot and dependency review own that process.
`OldTargetApi` remains visible as information for the deliberately validated target
API 36; all other enabled lint warnings remain fatal. See [ADR 0002](decisions/0002-target-sdk-lint-policy.md).

`assembleDebug` builds the installable application; `assembleDebugAndroidTest` compiles
the Compose launch test. `connectedDebugAndroidTest` executes that test using AndroidJUnitRunner
on a connected device/emulator. It checks the title, connection status and safety notice.
The source-manifest checks are supplemented by `scripts/Test-PackagedManifest.ps1`,
which runs after builds locally and in CI. It allows AndroidX's internal signature
permission, BLUETOOTH limited to API 30, BLUETOOTH_CONNECT and foreground COARSE/FINE
location. Backup remains disabled. Other permissions are rejected, including
discovery, background location and network access.

CI builds both APKs and uploads reports. Instrumentation execution is a separate required
validation until an emulator CI job is configured. Unit-test XML reports live under
`app/build/test-results/`; HTML reports under `app/build/reports/`.

## Next-milestone automation

`ObdConnectionTest` covers the fixed command set, parser boundaries and malformed
responses, initialization/discovery, unsupported PIDs, timeout, cancellation,
connection loss/reconnect and clearing values when switching sources. The focused
Android test `testAdapterConnectsAndDisconnectClearsReadings` exercises the new controls.
Real Bluetooth permissions, socket behavior and T5 values require hardware validation.

The first simulation delivery has three focused `SimulationTest` JVM tests covering
valid zero versus missing/unverified values, repeatable frames and relative-pressure
units, scenario replacement and cancellation when collection stops. Run them with
`testDebugUnitTest --tests com.machinedashboard.SimulationTest`.
The updated `FoundationLaunchTest` contains dashboard launch and missing-sensor UI
checks. Compiling those tests is separate from executing them on a device.

Milestone 1: simulation scenarios, availability, source isolation, ViewModel states,
telemetry transformations, and lifecycle behavior. Use virtual coroutine time for
delays and transitions. UI tests cover simulation indicators and unavailable sensors.

Milestone 2: bounded hex parsing, all four PID formulas and boundaries, wrong PID/mode,
incomplete bytes, multiple responders, discovery bitmaps, and adapter status responses
(`NO DATA`, `ERROR`, `STOPPED`, `UNABLE TO CONNECT`, `SEARCHING`, timeout).
Fake-transport integration tests cover initialization order, serialized commands,
timeout recovery, cancellation, disconnect and reconnect. UI tests cover permission
denial, device choice and session state. Include allowlist rejection tests for every
unsupported/state-changing operation.

Target at least 80% line and branch coverage for critical domain/protocol code when
introduced, with a scoped JaCoCo report and verification gate in milestone 1/2.
Foundation manifest tests do not establish domain or protocol coverage. Record actual
coverage; thresholds never replace meaningful negative-path tests.

## Device and visual checks

Record date, commit, device/API, configuration, expected result and observed result.
Launch in portrait and landscape, large font scaling, narrow split-screen and tablet
dimensions. Check scrolling, system insets, readable dark colors and safety notice.
Relaunch and background/foreground the app. Inspect the merged manifest for permissions.

## Parked-vehicle checklist for milestone 2

Use a stationary vehicle in a suitable ventilated location. A passenger or operator
must be able to end the test safely. Driving is unnecessary for initial validation.
Record adapter firmware/protocol in private notes, with identifying fields redacted.

- Adapter powered, ignition off: Bluetooth may connect; unavailable ECU is distinguished.
- Ignition on, engine stopped: protocol initialization/discovery works; RPM zero is valid.
- Engine running while parked: verify RPM, speed zero, coolant and calculated load against
  available reference instruments; record unsupported sensors explicitly.
- Disconnect Bluetooth/remove adapter: session exits cleanly and values become unavailable.
- ECU temporarily unavailable: timeout/protocol states remain distinct; UI stays responsive.
- Unsupported PID: skip unsupported polling and display unavailable.
- Background/foreground: verify cleanup, stale-data removal and explicit reconnection.
- Reconnect: fresh initialization and discovery, one request at a time, no old data.
- Deny/revoke connection permission: clear user-action state, no crash or scan/location request.
- Check allowed commands in opt-in redacted test tracing; ensure no vehicle state writes.

Hardware tests have not been executed. Record failures before preparing a release candidate.
