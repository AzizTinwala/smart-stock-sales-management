package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.adaptive.AdaptiveStatCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    trend: String? = null,
    isPositiveTrend: Boolean = true
) {
    AdaptiveStatCard(
        modifier = modifier,
        gradient = gradient,
        icon = {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.12f)
            ) {
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = iconColor
                    )
                }
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            trend?.let {
                AppText(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPositiveTrend) Color(0xFF22C55E) else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(AppTokens.Spacing.ExtraSmall))

        AppText(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.ExtraSmall))

        AppText(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
