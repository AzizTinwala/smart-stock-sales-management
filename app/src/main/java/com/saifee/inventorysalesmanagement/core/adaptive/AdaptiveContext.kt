package com.saifee.inventorysalesmanagement.core.adaptive

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.AdaptiveWindowSizeClass
import com.saifee.inventorysalesmanagement.core.enums.NavigationMode
import com.saifee.inventorysalesmanagement.core.enums.PaneMode

@Immutable
data class AdaptiveContext(
    val navigationMode: NavigationMode = NavigationMode.BOTTOM_BAR,
    val paneMode: PaneMode = PaneMode.SINGLE_PANE,
    val windowSizeClass: AdaptiveWindowSizeClass = AdaptiveWindowSizeClass.COMPACT,
    val isSmallScreen: Boolean = true,
    val screenWidth: Dp = 0.dp,
    val screenHeight: Dp = 0.dp,
    val isLandscape: Boolean = false,
    val spacingScale: Float = 1.0f,
    val typographyScale: Float = 1.0f
)

val LocalAdaptiveContext = staticCompositionLocalOf { AdaptiveContext() }
