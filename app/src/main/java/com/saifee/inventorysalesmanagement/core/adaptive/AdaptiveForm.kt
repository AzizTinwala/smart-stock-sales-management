package com.saifee.inventorysalesmanagement.core.adaptive

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.enums.AdaptiveWindowSizeClass

@Composable
fun AdaptiveForm(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val adaptiveContext = LocalAdaptiveContext.current
    val columnCount = when (adaptiveContext.windowSizeClass) {
        AdaptiveWindowSizeClass.EXPANDED -> 3
        AdaptiveWindowSizeClass.MEDIUM -> 2
        else -> 1
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTokens.Spacing.PagePadding),
        verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
    ) {
        content()
    }
}
