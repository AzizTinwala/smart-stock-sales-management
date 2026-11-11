package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.theme.stockColors

/**
 * High-density Inventory Distribution Overview Card
 */
@Composable
fun StockDistributionChart(
    totalItems: Int,
    lowStockItems: Int,
    outOfStockItems: Int,
    modifier: Modifier = Modifier
) {
    val stockColors = MaterialTheme.colorScheme.stockColors
    val inStockItems = (totalItems - lowStockItems - outOfStockItems).coerceAtLeast(0)

    val safeTotal = if (totalItems > 0) totalItems.toFloat() else 1f
    val inStockRatio = (inStockItems / safeTotal).coerceIn(0f, 1f)
    val lowStockRatio = (lowStockItems / safeTotal).coerceIn(0f, 1f)
    val outOfStockRatio = (outOfStockItems / safeTotal).coerceIn(0f, 1f)

    val cardShape = RoundedCornerShape(AppTokens.CornerRadius.Card)
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTokens.Elevation.None),
        modifier = modifier
            .fillMaxWidth()
            .border(width = AppTokens.Dimensions.BorderWidth, color = borderColor, shape = cardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTokens.Spacing.Medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Stock Status Distribution",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time warehouse health summary",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            // Multi-segment progress bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                if (inStockRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(inStockRatio)
                            .fillMaxHeight()
                            .background(stockColors.inStock)
                    )
                }
                if (lowStockRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(lowStockRatio)
                            .fillMaxHeight()
                            .background(stockColors.lowStock)
                    )
                }
                if (outOfStockRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(outOfStockRatio)
                            .fillMaxHeight()
                            .background(stockColors.outOfStock)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            // Legend metrics
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LegendItem(
                    label = "In Stock",
                    count = inStockItems,
                    ratio = inStockRatio,
                    color = stockColors.inStock
                )
                LegendItem(
                    label = "Low Stock",
                    count = lowStockItems,
                    ratio = lowStockRatio,
                    color = stockColors.lowStock
                )
                LegendItem(
                    label = "Out of Stock",
                    count = outOfStockItems,
                    ratio = outOfStockRatio,
                    color = stockColors.outOfStock
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    label: String,
    count: Int,
    ratio: Float,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$count (${(ratio * 100).toInt()}%)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
