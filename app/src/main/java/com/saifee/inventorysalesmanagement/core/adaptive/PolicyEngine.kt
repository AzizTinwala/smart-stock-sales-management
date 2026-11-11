package com.saifee.inventorysalesmanagement.core.adaptive

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.AdaptiveWindowSizeClass
import com.saifee.inventorysalesmanagement.core.enums.NavigationMode
import com.saifee.inventorysalesmanagement.core.enums.PaneMode

object PolicyEngine {
    
    fun calculateNavigationMode(width: Dp, height: Dp): NavigationMode {
        return when {
            width >= 1200.dp -> NavigationMode.PERMANENT_DRAWER
            width >= 840.dp -> NavigationMode.DRAWER
            width >= 600.dp -> NavigationMode.RAIL
            else -> NavigationMode.BOTTOM_BAR
        }
    }

    fun calculatePaneMode(width: Dp): PaneMode {
        return when {
            width >= 1200.dp -> PaneMode.THREE_PANE
            width >= 840.dp -> PaneMode.DUAL_PANE
            else -> PaneMode.SINGLE_PANE
        }
    }

    fun calculateWindowSizeClass(width: Dp): AdaptiveWindowSizeClass {
        return when {
            width >= 840.dp -> AdaptiveWindowSizeClass.EXPANDED
            width >= 600.dp -> AdaptiveWindowSizeClass.MEDIUM
            else -> AdaptiveWindowSizeClass.COMPACT
        }
    }
    
    fun calculateIsSmallScreen(width: Dp, height: Dp): Boolean {
        return width < 600.dp || height < 480.dp
    }
}
