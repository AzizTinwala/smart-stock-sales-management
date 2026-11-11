package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.saifee.inventorysalesmanagement.core.enums.StockStatus
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StockStatusBadgeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stockStatusBadge_rendersInStockLabel() {
        composeTestRule.setContent {
            InventorySalesManagementTheme {
                StockStatusBadge(status = StockStatus.IN_STOCK)
            }
        }

        composeTestRule.onNodeWithText("In Stock").assertIsDisplayed()
    }

    @Test
    fun stockStatusBadge_rendersLowStockLabel() {
        composeTestRule.setContent {
            InventorySalesManagementTheme {
                StockStatusBadge(status = StockStatus.LOW_STOCK)
            }
        }

        composeTestRule.onNodeWithText("Low Stock").assertIsDisplayed()
    }

    @Test
    fun stockStatusBadge_rendersOutOfStockLabel() {
        composeTestRule.setContent {
            InventorySalesManagementTheme {
                StockStatusBadge(status = StockStatus.OUT_OF_STOCK)
            }
        }

        composeTestRule.onNodeWithText("Out of Stock").assertIsDisplayed()
    }
}
