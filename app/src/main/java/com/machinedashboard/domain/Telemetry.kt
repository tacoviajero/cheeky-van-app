package com.machinedashboard.domain

import kotlinx.coroutines.flow.Flow

enum class Metric {
    ENGINE_RPM,
    SPEED,
    ENGINE_LOAD,
    COOLANT_TEMPERATURE,
    OIL_TEMPERATURE,
    BOOST_PRESSURE,
    MANIFOLD_PRESSURE,
    ATMOSPHERIC_PRESSURE,
    INTAKE_TEMPERATURE,
    AIR_FLOW,
    TURBO_RPM,
    REQUESTED_BOOST,
    TURBO_TEMPERATURE,
    VGT_POSITION
}

enum class UnavailableReason { NO_DATA, NOT_VERIFIED, UNSUPPORTED, MALFORMED, STALE }

sealed interface Reading {
    data class Value(val number: Double) : Reading {
        init {
            require(number.isFinite()) { "Telemetry must be finite" }
        }
    }

    data class Unavailable(val reason: UnavailableReason) : Reading
}

enum class TelemetryOrigin { SIMULATION, VEHICLE }

enum class SimulationScenario { ENGINE_STOPPED, RUNNING, MISSING_SENSOR }

data class TelemetrySample(
    val origin: TelemetryOrigin,
    val scenario: SimulationScenario?,
    val readings: Map<Metric, Reading>,
    val cluster: ClusterDetails? = null
) {
    operator fun get(metric: Metric): Reading =
        readings[metric] ?: Reading.Unavailable(UnavailableReason.NOT_VERIFIED)
}

/** Optional source-owned readings; never fill a vehicle sample with demo details. */
data class ClusterDetails(
    val batteryVoltage: Double? = null,
    val throttle: Double? = null,
    val consumption: Double? = null,
    val fuelPercent: Double? = null,
    val rangeKm: Double? = null,
    val tripA: Double? = null,
    val tripB: Double? = null,
    val odometer: Double? = null,
    val engineHours: Double? = null,
    val ambient: Double? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val sunsetMinute: Int? = null,
    val sunriseMinute: Int? = null,
    val gear: String? = null,
    val suggestedGear: Int? = null
)

interface TelemetryRepository {
    val samples: Flow<TelemetrySample>
}

interface TelemetryDataSource {
    fun sample(scenario: SimulationScenario, step: Int): TelemetrySample
}
