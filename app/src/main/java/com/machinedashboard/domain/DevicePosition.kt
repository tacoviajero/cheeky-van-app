package com.machinedashboard.domain

enum class PositionStatus { PERMISSION, DISABLED, SEARCHING, AVAILABLE, STALE }

data class DevicePosition(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val accuracy: Float?,
    val capturedAtNanos: Long,
    val approximate: Boolean = false,
    val mock: Boolean = false,
    val altitudeIsMsl: Boolean = false
) {
    fun isFreshAt(now: Long): Boolean = latitude.isFinite() && latitude in -90.0..90.0 &&
        longitude.isFinite() && longitude in -180.0..180.0 &&
        (altitude == null || altitude.isFinite()) &&
        capturedAtNanos >= 0 && now >= capturedAtNanos &&
        now - capturedAtNanos <= 60_000_000_000L
}

data class DevicePositionState(
    val status: PositionStatus = PositionStatus.PERMISSION,
    val fix: DevicePosition? = null
)
