package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun ActivityItem(
    title: String,
    subtitle: String,
    amount: String,
    time: String,
    logType: String,
    modifier: Modifier = Modifier
) {
    val (icon, baseColor) = when {
        logType.contains("SALE") -> Icons.Outlined.PointOfSale to Color(0xFF22C55E) // Green
        logType.contains("PURCHASE") -> Icons.Outlined.ShoppingBag to Color(0xFFEF4444) // Red
        logType.contains("INVENTORY") || logType.contains("STOCK") -> Icons.Outlined.Inventory2 to Color(0xFF3B82F6) // Blue
        logType.contains("VENDOR") -> Icons.Outlined.Storefront to (if (logType.contains("DELETED")) Color(0xFFEF4444) else Color(0xFF22C55E))
        logType.contains("CUSTOMER") -> Icons.Outlined.People to (if (logType.contains("DELETED")) Color(0xFFEF4444) else Color(0xFF22C55E))
        logType.contains("PAYMENT") -> Icons.Outlined.AccountBalanceWallet to (if (logType.contains("OUT")) Color(0xFFEF4444) else Color(0xFF22C55E))
        else -> Icons.Outlined.Info to Color(0xFF64748B) // Default Gray
    }

    val containerColor = baseColor.copy(alpha = 0.12f)
    val contentColor = baseColor // High contrast against 12% alpha background

    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = containerColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(AppTokens.Spacing.Medium))

            Column(modifier = Modifier.weight(1f)) {
                AppText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                AppText(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                if (amount.isNotEmpty()) {
                    AppText(
                        text = amount,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                AppText(
                    text = time,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
