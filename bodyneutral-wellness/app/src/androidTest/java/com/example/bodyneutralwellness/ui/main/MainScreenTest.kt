package com.example.bodyneutralwellness.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.example.bodyneutralwellness.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent { MainScreen(onItemClick = {}) }
  }

  @Test
  fun mainContent_exists() {
    composeTestRule.onNodeWithText("Bad Body Image Day Mode").assertExists()
    composeTestRule.onNodeWithText("Comfort & Gear Guide").assertExists()
    composeTestRule.onNodeWithText("Video Library").assertExists()
    composeTestRule.onNodeWithText("Body Scan Relaxation").assertExists()
    composeTestRule.onNodeWithText("Box Breathing").assertExists()
  }
}
