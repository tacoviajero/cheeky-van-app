# Architecture

## Foundation

Cheeky Van App serves our VW Transporter T5, 2.0 TDI CAAC, manual. Architecture,
dashboard behavior and performance work prioritize this personal vehicle. Interfaces
isolate simulation, parsing and transport for testing. Vehicle-specific readings must
be verified against the actual T5 and adapter before they enter the dashboard.

One Gradle `app` module keeps the build simple. Presentation now contains the Compose
dashboard and a ViewModel exposing StateFlow. Domain contains readings with explicit
availability/provenance and the repository contract. Data provides deterministic
simulation through that contract and coordinates OBD sessions. Transport provides
Bluetooth Classic RFCOMM and an ELM test adapter behind the same typed interface.

```text
app/src/main/java/com/machinedashboard/
  presentation/       Compose screens, ViewModels, UI state and actions
  domain/             Pure Kotlin models, business rules, repository contracts
  data/               Repository implementations, simulation, local storage
  transport/          RFCOMM lifecycle, bounded ELM framing, OBD protocol and parsing
app/src/test/         JVM regression, domain and protocol tests
app/src/androidTest/  Launch, interaction and platform tests
docs/decisions/       Short architecture decision records
```

## Dependency boundaries

Presentation depends on domain contracts. Data implements those contracts and uses
transport interfaces. Transport implements connection and command execution details;
parsing and scheduling remain separate from the socket implementation. Domain has no
Android or Compose dependency. The application composition root wires implementations
manually. Split modules only when boundaries or build performance justify it.

The first milestone 1 delivery implements immutable telemetry samples, a
`TelemetryRepository` contract, a simulated `TelemetryDataSource` and a ViewModel
exposing StateFlow. The activity wires constructors through a lightweight factory.
Compose collects with lifecycle awareness; polling stops when the screen has no active
collector. Scenario changes cancel the previous sequence and restart from its first
sample. Each sample carries provenance and explicit unavailable reasons. The live source
uses a connection contract; source changes atomically clear previous values.

Milestone 2 introduces `VehicleConnection`, `ObdTransport`, and a serialized session
coordinator. Commands are typed allowlisted operations converted into fixed wire
bytes. No strings from a response or UI become commands. Adapter configuration is
separately allowlisted and limited to safe initialization. Diagnostics get their own
repository when read-DTC behavior is introduced. DataStore and Room arrive with the
first real preferences and structured-history requirement respectively.

## Session and safety design

The approved horizontal cluster uses Compose for navigation, settings and semantic
interaction targets, with resolution-independent Canvas instruments for Inicio and
Tablero. Header and lower tabs retain native touch sizing. Optional `ClusterDetails`
belongs to its telemetry sample; live/fake OBD samples never inherit demonstration
fuel, gear, trips, GPS or temperatures. No new permissions or storage were introduced.
The fixed landscape manifest requirement is scoped to the owner's head unit; its
two orientation lint advisories are suppressed only on that activity attribute.
The body now fills its available viewport: cards and panels distribute horizontally,
while circular instruments move to their slots with uniform local scaling. Semantic
targets use the same independent horizontal/vertical positioning. Immersive mode
hides system bars with transient swipe access and preserves display-cutout clearance.

A single coroutine owns sequential requests and a configurable polling interval.
Connection state distinguishes disconnected, connecting, initializing, connected,
and typed failures. Socket I/O runs off the main thread. Cancellation and disconnect
must close the socket to unblock reads; source disposal and foreground/background
policy are tested before live use. Initial live sessions stop when the owning visible
lifecycle ends; resuming requires explicit reconnection until background behavior is defined.

The parser treats all response text as untrusted: bounded bytes and lines, strict
hex lengths, expected mode/PID and payload size, numeric checks, and explicit adapter
status handling. Identical duplicate ECU payloads are accepted; conflicting payloads
are rejected rather than choosing a responder silently. Unsupported,
temporarily missing, malformed, stale and valid-zero values remain distinct.

Use discovery before polling. No manufacturer-specific sensor assumptions. Distinguish
Bluetooth failures, protocol failures, timeouts and missing data. Clear old samples on
source/connection changes. Reconnection starts a fresh initialized session. Refresh
rates are measured on hardware before product commitments.
