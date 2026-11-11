package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.saifee.inventorysalesmanagement.core.enums.ButtonType
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appButton_displaysTextAndTriggersClick() {
        var clicked = false

        composeTestRule.setContent {
            InventorySalesManagementTheme {
                AppButton(
                    text = "Submit Invoice",
                    type = ButtonType.PRIMARY,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Submit Invoice").assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit Invoice").performClick()

        assertTrue(clicked)
    }

    @Test
    fun appButton_disabledState_preventsClick() {
        var clicked = false

        composeTestRule.setContent {
            InventorySalesManagementTheme {
                AppButton(
                    text = "Disabled Action",
                    enabled = false,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Disabled Action").assertIsNotEnabled()
        org.junit.Assert.assertFalse(clicked)
    }
}
