package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppGridListToggle(
    isGrid: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val options = listOf(
        Icons.AutoMirrored.Outlined.List to "List",
        Icons.Outlined.GridView to "Grid"
    )

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        options.forEachIndexed { index, (icon, label) ->
            SegmentedButton(
                selected = (isGrid && index == 1) || (!isGrid && index == 0),
                onClick = { onToggle(index == 1) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                icon = {
                    // Hide the default checkmark icon and use our own
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
