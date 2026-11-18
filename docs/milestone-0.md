# Milestone 0: Foundation acceptance

## Initial inspection

The workspace was empty and had no Git repository. Git, Python, Node and a standalone
ADB were on PATH. JDK, Gradle and Android SDK were absent. Git author identity was
unset; no GitHub remote URL was supplied. Standalone ADB failed to start its daemon.
Network downloads require execution outside the restricted network sandbox.

## Implementation sequence

1. Verify upstream compatibility and download checksum-verified local tooling.
2. Create a single-module Android project, Kotlin DSL and version catalog.
3. Add the minimal dark Compose launch screen with a safety notice.
4. Configure privacy regression tests, launch instrumentation, formatting and lint.
5. Add pinned CI, dependency updates and contribution conventions.
6. Execute available checks, inspect outputs and record evidence and blockers.
7. Prepare the feature branch for owner review. Begin simulation after approval.

## Acceptance gates

- Gradle wrapper validates against its published checksum and uses JDK 17.
- Project resolves stable pinned dependencies and compiles a debug APK.
- Kotlin/Gradle formatting, strict Android lint and JVM tests pass.
- Instrumentation test APK compiles; launch test runs on a device/emulator before sign-off.
- App displays availability and the manufacturer-instruments safety notice in a dark theme.
- Portrait and landscape, large fonts and safe-area behavior are checked on a device.
- The packaged manifest requests no sensitive platform permissions and disables Android
  backup. Only AndroidX's internal signature permission is permitted by the manifest check.
- Git conventions, PR workflow, least-privilege CI and required documentation are present.
- Hosted checks run once a remote exists; branch protection is verified in GitHub settings.
- Owner reviews the foundation before merging or authorizing milestone 1.

## Technical risks

- ELM327 clones differ in firmware, framing, timeouts and supported configuration commands.
- Generic PID availability on the T5 must be discovered and verified; an unavailable
  sensor must never be represented as a valid zero.
- Multiple ECU responders and protocol headers require parser fixtures from redacted data.
- Ignition-off adapters can connect over Bluetooth while the ECU remains unavailable.
- Closing RFCOMM sockets, cancelled reads, permission revocation and reconnect races
  require fake-transport tests plus parked-vehicle validation.
- Polling speed and power consumption remain unmeasured on the real adapter.
- Package identifier `com.machinedashboard` is provisional until publication ownership
  is settled. Confirm before distributing a signed release.

See [validation evidence](validation.md) for the actual completion state.
