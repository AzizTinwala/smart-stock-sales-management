package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.adaptive.LocalAdaptiveContext
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun RevenueCard(
    modifier: Modifier = Modifier,
    revenue: String = "₹24,500",
    growth: String = "+12.4%",
    progress: Float = 0.72f
) {
    val adaptiveContext = LocalAdaptiveContext.current
    val isNarrow = adaptiveContext.screenWidth < 360.dp

    AppCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppTokens.CornerRadius.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(AppTokens.Spacing.PagePadding)
        ) {

            Column(
                modifier = Modifier.padding(AppTokens.Spacing.PagePadding)
            ) {

                AppText(
                    "Monthly Revenue",
                    color = MaterialTheme.colorScheme.onPrimary.copy(.8f),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

                AppText(
                    revenue,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = if (isNarrow) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppTokens.Spacing.Small),
                    color = MaterialTheme.colorScheme.onPrimary,
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(.25f)
                )

                Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

                if (isNarrow) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(AppTokens.IconSize.Compact)
                            )
                            Spacer(modifier = Modifier.width(AppTokens.Spacing.ExtraSmall))
                            AppText(
                                growth,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        AppText(
                            "Compared to last month",
                            color = MaterialTheme.colorScheme.onPrimary.copy(.75f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )

                        Spacer(modifier = Modifier.width(AppTokens.Spacing.Small))

                        AppText(
                            growth,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(AppTokens.Spacing.Small))

                        AppText(
                            "Compared to last month",
                            color = MaterialTheme.colorScheme.onPrimary.copy(.75f)
                        )
                    }
                }
            }
        }
    }
}
