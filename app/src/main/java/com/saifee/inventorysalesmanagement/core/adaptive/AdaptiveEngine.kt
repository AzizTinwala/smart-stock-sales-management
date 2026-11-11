package com.saifee.inventorysalesmanagement.core.adaptive

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun AdaptiveEngine(
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val context = remember(screenWidth, screenHeight, isLandscape) {
        AdaptiveContext(
            navigationMode = PolicyEngine.calculateNavigationMode(screenWidth, screenHeight),
            paneMode = PolicyEngine.calculatePaneMode(screenWidth),
            windowSizeClass = PolicyEngine.calculateWindowSizeClass(screenWidth),
            isSmallScreen = PolicyEngine.calculateIsSmallScreen(screenWidth, screenHeight),
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            isLandscape = isLandscape,
            spacingScale = if (screenWidth < 360.dp) 0.85f else 1.0f,
            typographyScale = if (screenWidth < 360.dp) 0.9f else 1.0f
        )
    }

    CompositionLocalProvider(LocalAdaptiveContext provides context) {
        content()
    }
}
