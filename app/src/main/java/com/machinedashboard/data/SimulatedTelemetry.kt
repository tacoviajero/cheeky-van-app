package com.machinedashboard.data

import com.machinedashboard.domain.ClusterDetails
import com.machinedashboard.domain.Metric
import com.machinedashboard.domain.Reading
import com.machinedashboard.domain.SimulationScenario
import com.machinedashboard.domain.TelemetryDataSource
import com.machinedashboard.domain.TelemetryOrigin
import com.machinedashboard.domain.TelemetryRepository
import com.machinedashboard.domain.TelemetrySample
import com.machinedashboard.domain.UnavailableReason
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

/** Fictional demonstration data. These are not VW specifications or vehicle measurements. */
class SimulatedTelemetryDataSource : TelemetryDataSource {
    private val frames = listOf(
        doubleArrayOf(850.0, 0.0, 18.0, 72.0, 68.0, 100.0, 25.0, 9.0),
        doubleArrayOf(1400.0, 20.0, 35.0, 75.0, 70.0, 125.0, 28.0, 25.0),
        doubleArrayOf(2200.0, 45.0, 55.0, 80.0, 75.0, 170.0, 38.0, 55.0),
        doubleArrayOf(2600.0, 70.0, 65.0, 85.0, 80.0, 200.0, 44.0, 75.0),
        doubleArrayOf(1900.0, 55.0, 40.0, 88.0, 85.0, 150.0, 35.0, 40.0),
        doubleArrayOf(1000.0, 10.0, 20.0, 86.0, 86.0, 105.0, 27.0, 12.0)
    )

    override fun sample(scenario: SimulationScenario, step: Int): TelemetrySample {
        val frame = if (scenario == SimulationScenario.ENGINE_STOPPED) {
            doubleArrayOf(0.0, 0.0, 0.0, 20.0, 20.0, 100.0, 20.0, 0.0)
        } else {
            frames[Math.floorMod(step, frames.size)]
        }
        val values = listOf(
            Metric.ENGINE_RPM,
            Metric.SPEED,
            Metric.ENGINE_LOAD,
            Metric.COOLANT_TEMPERATURE,
            Metric.OIL_TEMPERATURE,
            Metric.MANIFOLD_PRESSURE,
            Metric.INTAKE_TEMPERATURE,
            Metric.AIR_FLOW
        ).mapIndexed { index, metric -> metric to Reading.Value(frame[index]) }.toMap()
        val readings: MutableMap<Metric, Reading> = values.toMutableMap()
        readings[Metric.ATMOSPHERIC_PRESSURE] = Reading.Value(100.0)
        readings[Metric.BOOST_PRESSURE] = Reading.Value((frame[5] - 100.0) / 100.0)
        if (scenario == SimulationScenario.MISSING_SENSOR) {
            readings[Metric.COOLANT_TEMPERATURE] = Reading.Unavailable(UnavailableReason.NO_DATA)
        }
        val stopped = scenario == SimulationScenario.ENGINE_STOPPED
        val index = Math.floorMod(step, frames.size)
        val details = ClusterDetails(
            batteryVoltage = if (stopped) 12.6 else 13.8,
            throttle = if (stopped) 0.0 else frame[2] / 2,
            consumption = if (stopped) 0.0 else 10.0,
            fuelPercent = 38.0,
            rangeKm = 286.0,
            tripA = 47.0,
            tripB = 128.6,
            odometer = 249546.0,
            engineHours = 1250.4,
            ambient = 22.5,
            sunsetMinute = 18 * 60 + 24,
            sunriseMinute = 6 * 60 + 48,
            gear = if (stopped) "N" else listOf("1", "2", "3", "3", "4", "R")[index],
            suggestedGear = if (stopped) {
                null
            } else {
                when (index) {
                    3 -> 4
                    4 -> 3
                    else -> null
                }
            }
        )
        return TelemetrySample(TelemetryOrigin.SIMULATION, scenario, readings.toMap(), details)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SimulatedTelemetryRepository(
    scenarios: StateFlow<SimulationScenario>,
    source: TelemetryDataSource = SimulatedTelemetryDataSource(),
    intervalMillis: Long = 1000
) : TelemetryRepository {
    init {
        require(intervalMillis > 0)
    }

    override val samples = scenarios.flatMapLatest { scenario ->
        flow {
            var step = 0
            while (currentCoroutineContext().isActive) {
                emit(source.sample(scenario, step++))
                delay(intervalMillis)
            }
        }
    }
}
