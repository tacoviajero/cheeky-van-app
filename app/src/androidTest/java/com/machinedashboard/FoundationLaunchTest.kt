package com.machinedashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.machinedashboard.presentation.MainActivity
import org.junit.Rule
import org.junit.Test

class FoundationLaunchTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchShowsConnectionAvailabilityAndSafetyNotice() {
        compose.onNodeWithText(compose.activity.getString(R.string.app_name)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.foundation_status))
            .assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.safety_notice))
            .assertExists()
    }
}
