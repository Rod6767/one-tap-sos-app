package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.service.LocationInfo
import com.example.ui.components.SosTriggerSection
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SosState
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun sos_main_screen_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        SosTriggerSection(
          sosState = SosState.Idle,
          locationInfo = LocationInfo(
            latitude = 37.774929,
            longitude = -122.419416,
            accuracy = 12f,
            address = "Market St, San Francisco, CA"
          ),
          primaryContact = null,
          onStartCountdown = {},
          onCancelCountdown = {},
          onDeactivateAlert = {},
          onToggleMute = {},
          onNavigateToTab = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/sos_main_screen.png")
  }
}

