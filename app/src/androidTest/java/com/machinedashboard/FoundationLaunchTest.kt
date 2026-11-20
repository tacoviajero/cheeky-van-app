package com.machinedashboard

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.machinedashboard.presentation.MainActivity
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FoundationLaunchTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchShowsSimulationAndVehicle() {
        compose.onNodeWithText(compose.activity.getString(R.string.app_name)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.simulation_badge))
            .assertIsDisplayed()
        saveSnapshot("inicio")
        assertEquals(
            Configuration.ORIENTATION_LANDSCAPE,
            compose.activity.resources.configuration.orientation
        )
        compose.onNodeWithTag("tab_1").performClick()
        compose.onNodeWithTag("value_ENGINE_RPM").assertExists()
        compose.onNodeWithTag("value_OIL_TEMPERATURE").assertExists()
        saveSnapshot("tablero")
        val headerBefore = compose.onNodeWithText(
            compose.activity.getString(R.string.app_name)
        ).fetchSemanticsNode().boundsInRoot
        compose.onNodeWithTag("value_FUEL").performClick()
        compose.onNodeWithTag("value_RANGE").assertExists()
        val headerAfter = compose.onNodeWithText(
            compose.activity.getString(R.string.app_name)
        ).fetchSemanticsNode().boundsInRoot
        assertEquals(headerBefore.left, headerAfter.left, .1f)
        assertEquals(headerBefore.top, headerAfter.top, .1f)
        compose.onNodeWithTag("back").performClick()
        compose.onNodeWithTag("tab_4").performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.safety_notice))
            .assertExists()
    }

    @Test
    fun missingScenarioKeepsSimulationExplicitAndShowsNoData() {
        compose.onNodeWithTag("tab_4").performClick()
        compose.onNodeWithTag("scenario_MISSING_SENSOR").performClick()
        compose.onNodeWithTag("tab_1").performClick()
        compose.waitUntil(timeoutMillis = 5000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.no_data))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("value_COOLANT_TEMPERATURE")
            .assertTextEquals(compose.activity.getString(R.string.no_data))
        compose.onNodeWithTag("value_MANIFOLD_PRESSURE").performClick()
        compose.onNodeWithTag("value_TURBO_RPM")
            .assertTextEquals(compose.activity.getString(R.string.unavailable_value))
        compose.onNodeWithText(compose.activity.getString(R.string.simulation_badge))
            .assertIsDisplayed()
    }

    @Test
    fun testAdapterConnectsAndDisconnectClearsReadings() {
        compose.onNodeWithTag("tab_4").performClick()
        compose.onNodeWithTag("test_adapter").performClick()
        compose.waitUntil(timeoutMillis = 6000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.connection_connected))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("tab_1").performClick()
        compose.waitUntil(timeoutMillis = 5000) {
            compose.onAllNodesWithText("1500").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(
            compose.activity.getString(R.string.test_adapter_badge)
        ).assertIsDisplayed()
        compose.onNodeWithTag("tab_4").performClick()
        compose.onNodeWithTag("disconnect").performClick()
        compose.waitUntil(timeoutMillis = 5000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.connection_disconnected))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("tab_1").performClick()
        compose.onNodeWithTag("value_ENGINE_RPM")
            .assertTextEquals(compose.activity.getString(R.string.unavailable_value))
    }

    private fun saveSnapshot(name: String) {
        if (android.os.Build.VERSION.SDK_INT < 26) return
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        File(compose.activity.cacheDir, "cluster-$name.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
