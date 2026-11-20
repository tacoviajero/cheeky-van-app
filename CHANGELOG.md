# Changelog

Meaningful changes follow Semantic Versioning and Conventional Commits.

## 0.1.0 — Unreleased

- Implement the approved horizontal Inicio/Tablero instruments, lower tabs, gear
  prompts and readout bars; move Bluetooth/scenario controls to Ajustes. Additional
  demo details stay isolated from real OBD samples; map/diagnostic functions remain pending.
- Add paired Bluetooth Classic selection, connection states and read-only OBD polling
  for RPM, speed, coolant and load, plus an ELM test adapter with simulated link loss.
  Live adapter/T5 validation remains pending.
- Add the T5 simulation dashboard for engine, oil and intake/boost readings, with
  explicit unavailable turbo sensors and three scenario controls.
- Separate telemetry models, simulated repository and lifecycle-aware ViewModel;
  prioritize the owner's real Car Scanner parameter catalog for future OBD work.
- Keep the API 36 target-SDK freshness advisory visible as information so hosted lint
  can validate the pinned foundation; other enabled lint warnings remain fatal.
- Correct the product name to Cheeky Van App and prioritize personal use with our VW T5.
- Establish Kotlin/Compose/Material 3 Android foundation and a minimal dark launch screen.
- Pin Gradle, plugins, libraries, and immutable GitHub Actions revisions.
- Add manifest privacy regression tests and a Compose launch instrumentation test.
- Configure formatting, strict Android lint, debug APK builds, and dependency review.
- Document architectural boundaries, acceptance gates, and parked-vehicle hardware checks.

This is a development version. Release validation and owner review remain required.
