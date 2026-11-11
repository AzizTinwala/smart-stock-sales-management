package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(AppTokens.CornerRadius.Large),
    contentPadding: PaddingValues = PaddingValues(AppTokens.Spacing.Medium),
    content: @Composable () -> Unit
) {

    AppCard(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppTokens.Elevation.Low
        ),
        contentPadding = contentPadding
    ) {
        content()
    }
}
