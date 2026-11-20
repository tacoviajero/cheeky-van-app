package com.machinedashboard.data

import com.machinedashboard.domain.ConnectionError
import com.machinedashboard.domain.ConnectionPhase
import com.machinedashboard.domain.ConnectionStatus
import com.machinedashboard.domain.ConnectionTarget
import com.machinedashboard.domain.Reading
import com.machinedashboard.domain.TelemetryOrigin
import com.machinedashboard.domain.TelemetrySample
import com.machinedashboard.domain.UnavailableReason
import com.machinedashboard.domain.VehicleConnection
import com.machinedashboard.transport.ObdCommand
import com.machinedashboard.transport.ObdException
import com.machinedashboard.transport.ObdProtocol
import com.machinedashboard.transport.ObdTransport
import com.machinedashboard.transport.TestObdTransport
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

class ObdVehicleConnection(
    private val factory: (ConnectionTarget) -> ObdTransport,
    private val commandTimeoutMillis: Long = 8000,
    private val pollIntervalMillis: Long = 1000
) : VehicleConnection {
    private val mutableStatus = MutableStateFlow(ConnectionStatus())
    private val mutableSamples = MutableStateFlow<TelemetrySample?>(null)
    override val status = mutableStatus.asStateFlow()
    override val samples = mutableSamples.asStateFlow()
    private val session = Mutex()

    @Volatile private var active: ObdTransport? = null

    @Volatile private var requestedDisconnect = false

    init {
        require(commandTimeoutMillis > 0 && pollIntervalMillis > 0)
    }

    override suspend fun run(target: ConnectionTarget) = session.withLock {
        requestedDisconnect = false
        val name = if (target is ConnectionTarget.Bluetooth) {
            target.adapter.name
        } else {
            "Adaptador de prueba"
        }
        mutableSamples.value = null
        mutableStatus.value = ConnectionStatus(ConnectionPhase.CONNECTING, name)
        var transport: ObdTransport? = null
        try {
            val connectedTransport = factory(target)
            transport = connectedTransport
            active = connectedTransport
            withTimeout(12000) { connectedTransport.open() }
            mutableStatus.value = ConnectionStatus(ConnectionPhase.INITIALIZING, name)
            suspend fun request(command: ObdCommand): String = withTimeout(commandTimeoutMillis) {
                connectedTransport.exchange(command)
            }
            for (command in ObdProtocol.initialization) {
                ObdProtocol.validateInitialization(command, request(command))
            }
            val supported = ObdProtocol.supported(request(ObdCommand.SUPPORTED))
            val protocol = ObdProtocol.protocol(request(ObdCommand.PROTOCOL))
            mutableStatus.value = ConnectionStatus(ConnectionPhase.CONNECTED, name, protocol)
            while (currentCoroutineContext().isActive && !requestedDisconnect) {
                val readings = ObdProtocol.telemetry.associate { command ->
                    requireNotNull(command.metric) to if (command in supported) {
                        ObdProtocol.reading(command, request(command))
                    } else {
                        Reading.Unavailable(UnavailableReason.UNSUPPORTED)
                    }
                }
                if (requestedDisconnect) break
                val origin = if (target is ConnectionTarget.Bluetooth) {
                    TelemetryOrigin.VEHICLE
                } else {
                    TelemetryOrigin.SIMULATION
                }
                mutableSamples.value = TelemetrySample(
                    origin,
                    null,
                    readings
                )
                delay(pollIntervalMillis)
            }
        } catch (_: TimeoutCancellationException) {
            fail(name, ConnectionError.TIMEOUT)
        } catch (error: CancellationException) {
            throw error
        } catch (_: SecurityException) {
            fail(name, ConnectionError.PERMISSION)
        } catch (error: ObdException) {
            fail(name, error.reason)
        } catch (_: IllegalArgumentException) {
            fail(name, ConnectionError.PROTOCOL)
        } catch (_: Exception) {
            fail(name, ConnectionError.CONNECTION)
        } finally {
            transport?.close()
            active = null
            mutableSamples.value = null
            if (status.value.phase !=
                ConnectionPhase.ERROR
            ) {
                mutableStatus.value = ConnectionStatus()
            }
        }
    }

    private fun fail(name: String, error: ConnectionError) {
        if (requestedDisconnect) return
        mutableSamples.value = null
        mutableStatus.value = ConnectionStatus(ConnectionPhase.ERROR, name, error = error)
    }

    override fun disconnect() {
        requestedDisconnect = true
        active?.close()
        mutableSamples.value = null
        mutableStatus.value = ConnectionStatus()
    }

    override fun simulateLoss() {
        (active as? TestObdTransport)?.loseConnection()
    }
}
