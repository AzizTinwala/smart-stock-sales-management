package com.saifee.inventorysalesmanagement.core.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens

@Composable
fun AdaptiveStatCard(
    modifier: Modifier = Modifier,
    gradient: Brush,
    icon: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val adaptiveContext = LocalAdaptiveContext.current
    val isNarrow = adaptiveContext.screenWidth < 400.dp
    
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(AppTokens.CornerRadius.Large)
    ) {
        Box(modifier = Modifier.background(gradient)) {
            if (isNarrow) {
                Column(
                    modifier = Modifier.padding(AppTokens.Spacing.Medium),
                    horizontalAlignment = Alignment.Start
                ) {
                    icon()
                    Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))
                    content()
                }
            } else {
                Row(
                    modifier = Modifier.padding(AppTokens.Spacing.Medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon()
                    Spacer(modifier = Modifier.width(AppTokens.Spacing.Medium))
                    Column {
                        content()
                    }
                }
            }
        }
    }
}
