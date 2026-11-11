package com.saifee.inventorysalesmanagement.core.ui.system

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.adaptive.LocalAdaptiveContext

object AppTokens {

    object Spacing {
        val ExtraSmall: Dp @Composable get() = 4.dp * LocalAdaptiveContext.current.spacingScale
        val Small: Dp @Composable get() = 8.dp * LocalAdaptiveContext.current.spacingScale
        val Medium: Dp @Composable get() = 16.dp * LocalAdaptiveContext.current.spacingScale
        val Large: Dp @Composable get() = 24.dp * LocalAdaptiveContext.current.spacingScale
        val ExtraLarge: Dp @Composable get() = 32.dp * LocalAdaptiveContext.current.spacingScale
        val PagePadding: Dp @Composable get() = 16.dp // StockPilot Spec: 16dp
    }

    object CornerRadius {
        val ExtraSmall = 4.dp
        val Small = 8.dp
        val Input = 10.dp      // StockPilot Spec: 10dp
        val Button = 10.dp     // StockPilot Spec: 10dp
        val Card = 12.dp       // StockPilot Spec: 12dp
        val Medium = 12.dp
        val Large = 16.dp
        val Badge = 100.dp     // Pill shape
        val ExtraLarge = 28.dp
    }

    object IconSize {
        val Compact = 18.dp
        val Default = 24.dp
        val Medium = 32.dp
        val Large = 48.dp
        val ExtraLarge = 64.dp
    }

    object Elevation {
        val None = 0.dp
        val Low = 1.dp
        val Default = 2.dp
        val High = 6.dp
    }

    object Dimensions {
        val MinTouchTarget = 48.dp // StockPilot Spec: Min 48dp touch target
        val BorderWidth = 1.dp     // Subtle border width
        val ButtonHeight = 48.dp   // Compact, accessible button height
    }
}
