package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"]) // required for some ViewModel tests
class DebugCrashTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun attemptToFindCrash() {
        ShadowLog.stream = System.out
        try {
            composeTestRule.setContent {
                NCLEXApp()
            }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("start_exam_button").performClick()
            composeTestRule.waitForIdle()
            
            // Click the first card
            composeTestRule.onNodeWithTag("submit_answer_button").assertExists()
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasClickAction())[1].performClick()
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithTag("submit_answer_button").performClick()
            composeTestRule.waitForIdle()
            
            composeTestRule.onNodeWithTag("next_question_button").performClick()
            composeTestRule.waitForIdle()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
