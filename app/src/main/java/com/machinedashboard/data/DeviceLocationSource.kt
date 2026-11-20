package com.machinedashboard.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.machinedashboard.domain.DevicePosition
import com.machinedashboard.domain.DevicePositionState
import com.machinedashboard.domain.PositionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Foreground-only device location. Coordinates are neither stored nor logged. */
class DeviceLocationSource(private val context: Context) {
    private val manager = context.getSystemService(LocationManager::class.java)
    private val handler = Handler(Looper.getMainLooper())
    private val mutableState = MutableStateFlow(DevicePositionState())
    val state = mutableState.asStateFlow()
    private var active = false
    private var precise = false
    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) = accept(location)
        override fun onProviderEnabled(provider: String) {
            if (active) start()
        }
        override fun onProviderDisabled(provider: String) {
            if (active) start()
        }

        @Deprecated("Required callback on Android 6–10")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
    }
    private val freshness = object : Runnable {
        override fun run() {
            if (!active) return
            val fix = state.value.fix
            if (fix != null && !fix.isFreshAt(SystemClock.elapsedRealtimeNanos())) {
                mutableState.value = DevicePositionState(PositionStatus.STALE)
            }
            handler.postDelayed(this, 5000)
        }
    }

    // Permission is checked immediately before access; revocation is handled below.
    @SuppressLint("MissingPermission")
    fun start() {
        stop()
        precise = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        val approximate = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        if (!precise && !approximate) {
            mutableState.value = DevicePositionState(PositionStatus.PERMISSION)
            return
        }
        val locationManager = manager ?: run {
            mutableState.value = DevicePositionState(PositionStatus.DISABLED)
            return
        }
        try {
            val providers = listOfNotNull(
                LocationManager.GPS_PROVIDER.takeIf { precise },
                LocationManager.NETWORK_PROVIDER
            ).filter {
                locationManager.allProviders.contains(it) &&
                    locationManager.isProviderEnabled(it)
            }
            if (providers.isEmpty()) {
                mutableState.value = DevicePositionState(PositionStatus.DISABLED)
                return
            }
            active = true
            mutableState.value = DevicePositionState(PositionStatus.SEARCHING)
            for (provider in providers) {
                locationManager.getLastKnownLocation(provider)?.let(::accept)
                locationManager.requestLocationUpdates(
                    provider,
                    5000L,
                    0f,
                    listener,
                    Looper.getMainLooper()
                )
            }
            handler.postDelayed(freshness, 5000)
        } catch (_: SecurityException) {
            stop()
            mutableState.value = DevicePositionState(PositionStatus.PERMISSION)
        } catch (_: IllegalArgumentException) {
            stop()
            mutableState.value = DevicePositionState(PositionStatus.DISABLED)
        }
    }

    @Suppress("DEPRECATION")
    private fun accept(location: Location) {
        if (!active) return
        val msl = precise && Build.VERSION.SDK_INT >= 34 && location.hasMslAltitude()
        val altitude = when {
            Build.VERSION.SDK_INT >= 34 && msl -> location.mslAltitudeMeters
            precise && location.hasAltitude() -> location.altitude
            else -> null
        }?.takeIf { it.isFinite() }
        val fix = DevicePosition(
            location.latitude,
            location.longitude,
            altitude,
            location.accuracy.takeIf { location.hasAccuracy() && it.isFinite() && it >= 0 },
            location.elapsedRealtimeNanos,
            approximate = !precise,
            mock = if (Build.VERSION.SDK_INT >=
                31
            ) {
                location.isMock
            } else {
                location.isFromMockProvider
            },
            altitudeIsMsl = msl
        )
        val now = SystemClock.elapsedRealtimeNanos()
        if (!fix.isFreshAt(now)) return
        val previous = state.value.fix
        if (previous != null && previous.isFreshAt(now) &&
            fix.capturedAtNanos < previous.capturedAtNanos
        ) {
            return
        }
        mutableState.value = DevicePositionState(PositionStatus.AVAILABLE, fix)
    }

    fun stop() {
        active = false
        handler.removeCallbacks(freshness)
        try {
            manager?.removeUpdates(listener)
        } catch (_: SecurityException) { }
        mutableState.value = DevicePositionState(PositionStatus.SEARCHING)
    }
}
