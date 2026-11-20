package com.machinedashboard

import androidx.lifecycle.viewModelScope
import com.machinedashboard.data.ObdVehicleConnection
import com.machinedashboard.data.SimulatedTelemetryDataSource
import com.machinedashboard.data.SimulatedTelemetryRepository
import com.machinedashboard.domain.Metric
import com.machinedashboard.domain.Reading
import com.machinedashboard.domain.SimulationScenario
import com.machinedashboard.domain.TelemetryOrigin
import com.machinedashboard.domain.UnavailableReason
import com.machinedashboard.presentation.DashboardViewModel
import com.machinedashboard.transport.TestObdTransport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimulationTest {
    private val source = SimulatedTelemetryDataSource()

    @Test
    fun validStoppedZeroIsDifferentFromMissingAndUnverifiedSensors() {
        val stopped = source.sample(SimulationScenario.ENGINE_STOPPED, 0)
        val missing = source.sample(SimulationScenario.MISSING_SENSOR, 0)
        assertEquals(Reading.Value(0.0), stopped[Metric.ENGINE_RPM])
        assertEquals(Reading.Value(0.0), stopped[Metric.SPEED])
        assertEquals(
            Reading.Unavailable(UnavailableReason.NO_DATA),
            missing[Metric.COOLANT_TEMPERATURE]
        )
        assertTrue(missing[Metric.SPEED] is Reading.Value)
        for (metric in listOf(
            Metric.TURBO_RPM,
            Metric.REQUESTED_BOOST,
            Metric.TURBO_TEMPERATURE,
            Metric.VGT_POSITION
        )) {
            assertEquals(Reading.Unavailable(UnavailableReason.NOT_VERIFIED), stopped[metric])
        }
    }

    @Test
    fun demoSequenceIsRepeatableAndPressureUsesItsDeclaredReference() {
        val first = source.sample(SimulationScenario.RUNNING, 0)
        val accelerating = source.sample(SimulationScenario.RUNNING, 3)
        assertEquals(first, source.sample(SimulationScenario.RUNNING, 6))
        assertNotEquals(first[Metric.SPEED], accelerating[Metric.SPEED])
        assertEquals(Reading.Value(1.0), accelerating[Metric.BOOST_PRESSURE])
        assertEquals(Reading.Value(200.0), accelerating[Metric.MANIFOLD_PRESSURE])
        assertEquals(Reading.Value(100.0), accelerating[Metric.ATMOSPHERIC_PRESSURE])
        for (scenario in SimulationScenario.entries) {
            assertEquals(TelemetryOrigin.SIMULATION, source.sample(scenario, 4).origin)
        }
    }

    @Test
    fun scenarioChangeReplacesTheOldSequenceAndCollectionStopsWithTheScreen() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val scenarios = MutableStateFlow(SimulationScenario.ENGINE_STOPPED)
        var reads = 0
        val countingSource = object : com.machinedashboard.domain.TelemetryDataSource {
            override fun sample(scenario: SimulationScenario, step: Int) =
                source.sample(scenario, step).also { reads++ }
        }
        val vm =
            DashboardViewModel(
                scenarios,
                SimulatedTelemetryRepository(scenarios, countingSource),
                ObdVehicleConnection(factory = { TestObdTransport(0) })
            )
        try {
            val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.uiState.collect { }
            }
            runCurrent()
            assertEquals(Reading.Value(0.0), vm.uiState.value.sample?.get(Metric.ENGINE_RPM))
            vm.selectScenario(SimulationScenario.RUNNING)
            runCurrent()
            assertEquals(Reading.Value(850.0), vm.uiState.value.sample?.get(Metric.ENGINE_RPM))
            advanceTimeBy(1000)
            runCurrent()
            assertEquals(Reading.Value(20.0), vm.uiState.value.sample?.get(Metric.SPEED))
            vm.selectScenario(SimulationScenario.MISSING_SENSOR)
            runCurrent()
            assertEquals(
                Reading.Unavailable(UnavailableReason.NO_DATA),
                vm.uiState.value.sample?.get(Metric.COOLANT_TEMPERATURE)
            )
            assertEquals(Reading.Value(0.0), vm.uiState.value.sample?.get(Metric.SPEED))
            collector.cancel()
            runCurrent()
            val readsAfterStop = reads
            advanceTimeBy(5000)
            runCurrent()
            assertEquals(readsAfterStop, reads)
        } finally {
            vm.viewModelScope.cancel()
            Dispatchers.resetMain()
        }
    }
}
