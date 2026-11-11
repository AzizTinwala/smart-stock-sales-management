package com.saifee.inventorysalesmanagement.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.saifee.inventorysalesmanagement.core.adaptive.AdaptiveEngine
import com.saifee.inventorysalesmanagement.core.enums.AppTheme

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    outline = LightOutline,
    outlineVariant = LightBorder,
    tertiaryContainer = LightWarningContainer,
    onTertiaryContainer = LightOnWarningContainer,
    error = LightError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    outline = DarkOutline,
    outlineVariant = DarkBorder,
    tertiaryContainer = DarkWarningContainer,
    onTertiaryContainer = DarkOnWarningContainer,
    error = DarkError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer
)

// Semantic Stock Colors Extension for StockPilot
data class StockColors(
    val inStock: Color,
    val inStockContainer: Color,
    val onInStockContainer: Color,
    val lowStock: Color,
    val lowStockContainer: Color,
    val onLowStockContainer: Color,
    val outOfStock: Color,
    val outOfStockContainer: Color,
    val onOutOfStockContainer: Color,
    val border: Color
)

val ColorScheme.stockColors: StockColors
    @Composable
    @ReadOnlyComposable
    get() {
        val isDark = isSystemInDarkTheme() // or based on surface light/dark check
        return if (isDark) {
            StockColors(
                inStock = DarkSuccess,
                inStockContainer = DarkSuccessContainer,
                onInStockContainer = DarkOnSuccessContainer,
                lowStock = DarkWarning,
                lowStockContainer = DarkWarningContainer,
                onLowStockContainer = DarkOnWarningContainer,
                outOfStock = DarkError,
                outOfStockContainer = DarkErrorContainer,
                onOutOfStockContainer = DarkOnErrorContainer,
                border = DarkBorder
            )
        } else {
            StockColors(
                inStock = LightSuccess,
                inStockContainer = LightSuccessContainer,
                onInStockContainer = LightOnSuccessContainer,
                lowStock = LightWarning,
                lowStockContainer = LightWarningContainer,
                onLowStockContainer = LightOnWarningContainer,
                outOfStock = LightError,
                outOfStockContainer = LightErrorContainer,
                onOutOfStockContainer = LightOnErrorContainer,
                border = LightBorder
            )
        }
    }

@Composable
fun InventorySalesManagementTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MonolithTypography,
        shapes = MonolithShapes
    ) {
        AdaptiveEngine {
            content()
        }
    }
}
