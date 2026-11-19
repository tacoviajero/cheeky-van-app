package com.machinedashboard.presentation

internal data class SolarEvent(val minute: Int, val sunrise: Boolean, val tomorrow: Boolean = false)

/** Chooses the next event from the source's local daily solar schedule. */
internal fun nextSolarEvent(now: Int, sunrise: Int?, sunset: Int?): SolarEvent? {
    if (now !in 0..1439 || sunrise == null || sunset == null ||
        sunrise !in 0..1439 || sunset !in 0..1439 || sunrise >= sunset
    ) {
        return null
    }
    return when {
        now < sunrise -> SolarEvent(sunrise, sunrise = true)
        now < sunset -> SolarEvent(sunset, sunrise = false)
        else -> SolarEvent(sunrise, sunrise = true, tomorrow = true)
    }
}
