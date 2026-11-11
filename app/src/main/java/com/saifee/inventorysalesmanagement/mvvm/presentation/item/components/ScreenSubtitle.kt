package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun ScreenSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {

    AppText(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

}
