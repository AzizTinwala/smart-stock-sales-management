package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(headline) },
        supportingContent = supportingText?.let { { Text(it) } },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    )
}
