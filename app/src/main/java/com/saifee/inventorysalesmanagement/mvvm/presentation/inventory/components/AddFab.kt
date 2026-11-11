package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddFab(
    onClick: () -> Unit
) {

    ExtendedFloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {

        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            modifier = Modifier.size(34.dp)
        )

    }

}