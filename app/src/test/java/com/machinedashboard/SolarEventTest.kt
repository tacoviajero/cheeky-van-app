package com.machinedashboard

import com.machinedashboard.presentation.SolarEvent
import com.machinedashboard.presentation.nextSolarEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SolarEventTest {
    private val rise = 6 * 60 + 48
    private val set = 18 * 60 + 24

    @Test
    fun beforeDawnShowsSunriseAndAfterDawnShowsSunset() {
        assertEquals(SolarEvent(rise, true), nextSolarEvent(0, rise, set))
        assertEquals(SolarEvent(rise, true), nextSolarEvent(rise - 1, rise, set))
        assertEquals(SolarEvent(set, false), nextSolarEvent(rise, rise, set))
        assertEquals(SolarEvent(set, false), nextSolarEvent(set - 1, rise, set))
    }

    @Test
    fun atSunsetAndAfterItShowsTheFollowingSunrise() {
        assertEquals(SolarEvent(rise, true, true), nextSolarEvent(set, rise, set))
        assertEquals(SolarEvent(rise, true, true), nextSolarEvent(23 * 60 + 59, rise, set))
    }

    @Test
    fun missingOrInvalidSchedulesRemainUnavailable() {
        assertNull(nextSolarEvent(600, null, set))
        assertNull(nextSolarEvent(600, rise, null))
        assertNull(nextSolarEvent(600, set, rise))
        assertNull(nextSolarEvent(600, -1, set))
        assertNull(nextSolarEvent(1440, rise, set))
    }
}
