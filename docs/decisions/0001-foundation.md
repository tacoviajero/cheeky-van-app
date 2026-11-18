# ADR 0001: A single Android module with explicit package boundaries

Date: 2026-11-16
Status: Accepted for initial implementation; owner foundation review pending.

## Context

Cheeky Van App is a personal Android application optimized for our VW Transporter T5
and its ELM327-compatible adapter, with independent simulation and transport testing.

## Decision

Use Kotlin, Compose/Material 3, Gradle Kotlin DSL and a version catalog in one `app`
module. Introduce presentation, domain, data and transport packages as behavior arrives.
Use manual constructor injection. Future state uses MVVM with unidirectional actions,
Coroutines and StateFlow. Add lifecycle, storage and DI libraries when required.

Keep the first milestone limited to project setup, a launch screen and validation
infrastructure. The app requests no permissions. Read-only vehicle commands will be
enforced by typed allowlists before any live connection implementation.

## Consequences

The first build stays small, with fewer compatibility constraints. Package boundaries
need review until separate modules become useful. JVM privacy tests cover the source
manifest; merged-manifest inspection and instrumentation validate the packaged app.
Device execution and owner review remain release gates.
