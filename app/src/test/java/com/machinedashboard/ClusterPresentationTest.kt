package com.machinedashboard

import com.machinedashboard.data.SimulatedTelemetryDataSource
import com.machinedashboard.domain.SimulationScenario
import com.machinedashboard.presentation.gaugeFraction
import com.machinedashboard.presentation.gearDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ClusterPresentationTest {
    @Test
    fun dialAndIndicatorShareTheSameClampedScaleAndPreserveMissing() {
        assertEquals(.3f, gaugeFraction(72.0, 240.0))
        assertEquals(0f, gaugeFraction(0.0, 6000.0))
        assertEquals(1f, gaugeFraction(7000.0, 6000.0))
        assertEquals(0f, gaugeFraction(-1.0, 100.0))
        assertNull(gaugeFraction(null, 240.0))
        assertNull(gaugeFraction(Double.NaN, 240.0))
        assertNull(gaugeFraction(1.0, 0.0))
    }

    @Test
    fun suggestionsAreConditionalAndUseTheCorrectSide() {
        assertEquals(1, gearDirection("3", 4))
        assertEquals(-1, gearDirection("4", 3))
        assertNull(gearDirection("3", 3))
        assertNull(gearDirection("3", null))
        assertNull(gearDirection("R", 1))
        assertNull(gearDirection("N", 1))
        assertNull(gearDirection(null, 1))
        assertNull(gearDirection("3", 7))
    }

    @Test
    fun demoExercisesBothPromptsReverseAndTheApprovedSupportingReadings() {
        val source = SimulatedTelemetryDataSource()
        val up = source.sample(SimulationScenario.RUNNING, 3).cluster
        val down = source.sample(SimulationScenario.RUNNING, 4).cluster
        val reverse = source.sample(SimulationScenario.RUNNING, 5).cluster
        assertEquals(1, gearDirection(up?.gear, up?.suggestedGear))
        assertEquals(-1, gearDirection(down?.gear, down?.suggestedGear))
        assertEquals("R", reverse?.gear)
        assertNull(reverse?.suggestedGear)
        assertEquals(13.8, up?.batteryVoltage)
        assertEquals(38.0, up?.fuelPercent)
        assertEquals(47.0, up?.tripA)
        assertEquals(128.6, up?.tripB)
    }
}
