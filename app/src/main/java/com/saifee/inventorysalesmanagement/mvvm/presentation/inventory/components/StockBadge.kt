package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun StockBadge(
    priority: Boolean,
    lowStock: Boolean
) {

    when {

        priority -> {

            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    )
                    .padding(
                        horizontal = AppTokens.Spacing.Medium,
                        vertical = AppTokens.Spacing.Small
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Outlined.StarBorder,
                    null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(AppTokens.IconSize.Compact)
                )

                Spacer(modifier = Modifier.width(AppTokens.Spacing.Small))

                AppText(
                    "PRIORITY",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )

            }
        }

        lowStock -> {

            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                        CircleShape
                    )
                    .padding(
                        horizontal = AppTokens.Spacing.Medium,
                        vertical = AppTokens.Spacing.Small
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Outlined.Warning,
                    null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(AppTokens.IconSize.Compact)
                )

                Spacer(modifier = Modifier.width(AppTokens.Spacing.Small))

                AppText(
                    "LOW STOCK",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )

            }

        }

    }

}