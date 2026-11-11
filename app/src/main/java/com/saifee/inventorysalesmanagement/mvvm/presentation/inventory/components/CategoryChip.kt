package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun CategoryChip(
    title: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTokens.Spacing.PagePadding),
        verticalAlignment = Alignment.CenterVertically
    ) {

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Box(
            modifier = Modifier
                .padding(horizontal = AppTokens.Spacing.Small)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    CircleShape
                )
                .padding(
                    horizontal = AppTokens.Spacing.Medium,
                    vertical = AppTokens.Spacing.Small
                )
        ) {

            AppText(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

        }

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}