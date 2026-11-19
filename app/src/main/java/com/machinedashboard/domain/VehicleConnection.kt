package com.machinedashboard.domain

import kotlinx.coroutines.flow.StateFlow

data class PairedAdapter(val id: String, val name: String)

sealed interface ConnectionTarget {
    data object TestAdapter : ConnectionTarget
    data class Bluetooth(val adapter: PairedAdapter) : ConnectionTarget
}

enum class ConnectionPhase { DISCONNECTED, CONNECTING, INITIALIZING, CONNECTED, ERROR }
enum class ConnectionError {
    BLUETOOTH_OFF,
    PERMISSION,
    CONNECTION,
    PROTOCOL,
    VEHICLE_UNAVAILABLE,
    TIMEOUT
}

data class ConnectionStatus(
    val phase: ConnectionPhase = ConnectionPhase.DISCONNECTED,
    val adapterName: String = "",
    val protocol: String = "",
    val error: ConnectionError? = null
)

interface VehicleConnection {
    val status: StateFlow<ConnectionStatus>
    val samples: StateFlow<TelemetrySample?>
    suspend fun run(target: ConnectionTarget)
    fun disconnect()
    fun simulateLoss()
}
