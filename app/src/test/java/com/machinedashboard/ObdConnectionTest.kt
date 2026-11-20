package com.machinedashboard

import androidx.lifecycle.viewModelScope
import com.machinedashboard.data.ObdVehicleConnection
import com.machinedashboard.data.SimulatedTelemetryRepository
import com.machinedashboard.domain.ConnectionError
import com.machinedashboard.domain.ConnectionPhase
import com.machinedashboard.domain.ConnectionTarget
import com.machinedashboard.domain.Metric
import com.machinedashboard.domain.PairedAdapter
import com.machinedashboard.domain.Reading
import com.machinedashboard.domain.SimulationScenario
import com.machinedashboard.domain.TelemetryOrigin
import com.machinedashboard.domain.UnavailableReason
import com.machinedashboard.presentation.DashboardSource
import com.machinedashboard.presentation.DashboardViewModel
import com.machinedashboard.transport.ObdCommand
import com.machinedashboard.transport.ObdProtocol
import com.machinedashboard.transport.ObdTransport
import com.machinedashboard.transport.TestObdTransport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObdConnectionTest {
    @Test
    fun validatesFramesBoundariesAndUnavailableResponses() {
        assertEquals(
            Reading.Value(16383.75),
            ObdProtocol.reading(ObdCommand.RPM, "010C\r41 0C FF FF\r>")
        )
        assertEquals(Reading.Value(0.0), ObdProtocol.reading(ObdCommand.SPEED, "410D00"))
        assertEquals(Reading.Value(-40.0), ObdProtocol.reading(ObdCommand.COOLANT, "410500"))
        assertEquals(Reading.Value(100.0), ObdProtocol.reading(ObdCommand.LOAD, "4104FF"))
        for (response in listOf("NO DATA", "STOPPED")) {
            assertEquals(
                Reading.Unavailable(UnavailableReason.NO_DATA),
                ObdProtocol.reading(ObdCommand.RPM, response)
            )
        }
        val malformed = listOf(
            "410C0",
            "410C000000",
            "410D00",
            "410CZZZZ",
            "410C0000\r410C0004",
            "ERROR",
            "?"
        )
        for (response in malformed) {
            assertEquals(
                Reading.Unavailable(UnavailableReason.MALFORMED),
                ObdProtocol.reading(ObdCommand.RPM, response)
            )
        }
        assertThrows(IllegalArgumentException::class.java) { ObdProtocol.supported("4100FFFF") }
        assertThrows(IllegalArgumentException::class.java) { ObdProtocol.protocol("A0") }
        val allowed = setOf(
            "ATZ", "ATE0", "ATL0", "ATS0", "ATH0", "ATSP0", "ATDPN",
            "0100", "010C", "010D", "0105", "0104"
        )
        assertTrue(ObdCommand.entries.all { it.wire in allowed })
    }

    @Test
    fun initializesAndPollsSequentialReadOnlyCommands() = runTest {
        val transport = TestObdTransport(10)
        val connection = ObdVehicleConnection(factory = { transport })
        val job = backgroundScope.launch { connection.run(ConnectionTarget.TestAdapter) }
        runCurrent()
        assertEquals(ConnectionPhase.CONNECTING, connection.status.value.phase)
        advanceTimeBy(15)
        runCurrent()
        assertEquals(ConnectionPhase.INITIALIZING, connection.status.value.phase)
        advanceTimeBy(150)
        runCurrent()
        assertEquals(ConnectionPhase.CONNECTED, connection.status.value.phase)
        assertEquals(
            ObdProtocol.initialization + listOf(ObdCommand.SUPPORTED, ObdCommand.PROTOCOL) +
                ObdProtocol.telemetry,
            transport.commands
        )
        assertEquals(Reading.Value(1500.0), connection.samples.value?.get(Metric.ENGINE_RPM))
        assertEquals(TelemetryOrigin.SIMULATION, connection.samples.value?.origin)
        job.cancelAndJoin()
        assertNull(connection.samples.value)
        assertEquals(ConnectionPhase.DISCONNECTED, connection.status.value.phase)
    }

    @Test
    fun lossClearsValuesAndReconnectionStartsAFreshSession() = runTest {
        var sessions = 0
        val connection = ObdVehicleConnection(factory = {
            sessions++
            TestObdTransport(0)
        })
        val first = backgroundScope.launch { connection.run(ConnectionTarget.TestAdapter) }
        runCurrent()
        assertNotNull(connection.samples.value)
        connection.simulateLoss()
        advanceTimeBy(1100)
        runCurrent()
        assertEquals(ConnectionError.CONNECTION, connection.status.value.error)
        assertNull(connection.samples.value)
        first.join()
        val second = backgroundScope.launch { connection.run(ConnectionTarget.TestAdapter) }
        runCurrent()
        assertEquals(2, sessions)
        assertEquals(ConnectionPhase.CONNECTED, connection.status.value.phase)
        second.cancelAndJoin()
        assertNull(connection.samples.value)
    }

    @Test
    fun timeoutClosesTransportAndUnsupportedPidsAreNotPolled() = runTest {
        var closed = false
        val delayed = object : ObdTransport {
            override suspend fun open() { }
            override suspend fun exchange(command: ObdCommand): String {
                delay(5000)
                return "OK"
            }
            override fun close() {
                closed = true
            }
        }
        val timeout = ObdVehicleConnection(factory = { delayed }, commandTimeoutMillis = 100)
        val timed = backgroundScope.launch { timeout.run(ConnectionTarget.TestAdapter) }
        advanceTimeBy(200)
        runCurrent()
        assertEquals(ConnectionError.TIMEOUT, timeout.status.value.error)
        assertTrue(closed)
        timed.join()
        val noPids = TestObdTransport(0, "00000000")
        val unsupported = ObdVehicleConnection(factory = { noPids })
        val job = backgroundScope.launch { unsupported.run(ConnectionTarget.TestAdapter) }
        runCurrent()
        assertEquals(
            Reading.Unavailable(UnavailableReason.UNSUPPORTED),
            unsupported.samples.value?.get(Metric.SPEED)
        )
        assertTrue(noPids.commands.none { it in ObdProtocol.telemetry })
        job.cancelAndJoin()
    }

    @Test
    fun switchingToVehicleNeverKeepsDemoValues() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val scenarios = MutableStateFlow(SimulationScenario.RUNNING)
        val connection = ObdVehicleConnection(factory = { TestObdTransport(10) })
        val vm = DashboardViewModel(scenarios, SimulatedTelemetryRepository(scenarios), connection)
        try {
            val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.uiState.collect { }
            }
            runCurrent()
            assertEquals(TelemetryOrigin.SIMULATION, vm.uiState.value.sample?.origin)
            vm.connect(ConnectionTarget.Bluetooth(PairedAdapter("test", "Prueba")))
            runCurrent()
            assertEquals(DashboardSource.BLUETOOTH, vm.uiState.value.source)
            assertNull(vm.uiState.value.sample)
            advanceTimeBy(200)
            runCurrent()
            assertEquals(TelemetryOrigin.VEHICLE, vm.uiState.value.sample?.origin)
            vm.disconnect()
            runCurrent()
            assertNull(vm.uiState.value.sample)
            vm.showDemo()
            runCurrent()
            assertEquals(DashboardSource.DEMO, vm.uiState.value.source)
            assertEquals(TelemetryOrigin.SIMULATION, vm.uiState.value.sample?.origin)
            collector.cancel()
        } finally {
            vm.viewModelScope.cancel()
            runCurrent()
            Dispatchers.resetMain()
        }
    }
}
