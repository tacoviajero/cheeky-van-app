package com.machinedashboard

import com.machinedashboard.domain.DevicePosition
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DevicePositionTest {
    private val now = 120_000_000_000L

    @Test
    fun validZeroCoordinatesAndSeaLevelAreRealValues() {
        assertTrue(DevicePosition(0.0, 0.0, 0.0, 5f, now).isFreshAt(now))
        assertTrue(DevicePosition(-90.0, -180.0, null, null, now).isFreshAt(now))
    }

    @Test
    fun staleFutureAndInvalidCoordinatesAreRejected() {
        assertFalse(DevicePosition(10.0, 20.0, null, null, 0).isFreshAt(now))
        assertFalse(DevicePosition(10.0, 20.0, null, null, now + 1).isFreshAt(now))
        assertFalse(DevicePosition(91.0, 20.0, null, null, now).isFreshAt(now))
        assertFalse(DevicePosition(10.0, 181.0, null, null, now).isFreshAt(now))
        assertFalse(DevicePosition(Double.NaN, 20.0, null, null, now).isFreshAt(now))
        assertFalse(DevicePosition(10.0, 20.0, Double.NaN, null, now).isFreshAt(now))
    }

    @Test
    fun freshnessExpiresAfterOneMinute() {
        val fix = DevicePosition(10.0, 20.0, null, 100f, now, approximate = true)
        assertTrue(fix.isFreshAt(now + 60_000_000_000L))
        assertFalse(fix.isFreshAt(now + 60_000_000_001L))
    }
}
