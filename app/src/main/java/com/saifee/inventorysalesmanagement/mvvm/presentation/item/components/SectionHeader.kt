package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    background: Color,
    tint: Color = MaterialTheme.colorScheme.primary
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(AppTokens.IconSize.Medium)
                .background(
                    background,
                    RoundedCornerShape(AppTokens.CornerRadius.Small)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint
            )

        }

        Spacer(modifier = Modifier.width(AppTokens.Spacing.Medium))

        AppText(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )

    }

}
