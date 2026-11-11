package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.mvvm.core.enums.TransactionType
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ActivityLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecentActivitySection(
    logs: List<ActivityLogEntity>,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppTokens.Spacing.Large)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AppText(
                text = "Recent Activity",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = { }
            ) {

                AppText("View All")

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null
                )

            }

        }

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Large))

        if (logs.isEmpty()) {
            AppText(
                text = "No recent activity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = AppTokens.Spacing.Large)
            )
        } else {
            logs.forEach { log ->
                val type = when (log.transactionType) {
                    "INCOME" -> TransactionType.INCOME
                    "EXPENSE" -> TransactionType.EXPENSE
                    else -> TransactionType.INCOME // Default
                }
                
                ActivityItem(
                    title = log.type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                    subtitle = log.message,
                    amount = if (log.amount != null) "${if (type == TransactionType.INCOME) "+" else "-"}₹${log.amount}" else "",
                    time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(log.createdAt)),
                    logType = log.type
                )

                Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))
            }
        }
    }
}
