package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.StockStatus
import com.saifee.inventorysalesmanagement.core.ui.theme.MonolithPillShape
import com.saifee.inventorysalesmanagement.core.ui.theme.stockColors

/**
 * Enterprise Stock Status Badge (Pill style) with semantic colors & indicator dot.
 */
@Composable
fun StockStatusBadge(
    status: StockStatus,
    modifier: Modifier = Modifier,
    showDot: Boolean = true
) {
    val stockColors = MaterialTheme.colorScheme.stockColors

    val (label, containerColor, contentColor, dotColor) = when (status) {
        StockStatus.IN_STOCK -> Quadruple(
            "In Stock",
            stockColors.inStockContainer,
            stockColors.onInStockContainer,
            stockColors.inStock
        )
        StockStatus.LOW_STOCK -> Quadruple(
            "Low Stock",
            stockColors.lowStockContainer,
            stockColors.onLowStockContainer,
            stockColors.lowStock
        )
        StockStatus.OUT_OF_STOCK -> Quadruple(
            "Out of Stock",
            stockColors.outOfStockContainer,
            stockColors.onOutOfStockContainer,
            stockColors.outOfStock
        )
    }

    StatusBadge(
        label = label,
        containerColor = containerColor,
        contentColor = contentColor,
        dotColor = if (showDot) dotColor else null,
        modifier = modifier
    )
}

/**
 * Generic Status Badge component for custom enterprise statuses (e.g. Transaction types, Active/Inactive, etc.)
 */
@Composable
fun StatusBadge(
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    dotColor: Color? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = containerColor, shape = MonolithPillShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        if (dotColor != null) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color = dotColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor
        )
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
