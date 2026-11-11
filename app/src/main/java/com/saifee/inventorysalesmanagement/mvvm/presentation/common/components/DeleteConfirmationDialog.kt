package com.saifee.inventorysalesmanagement.mvvm.presentation.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.saifee.inventorysalesmanagement.core.enums.ButtonType
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "Confirm Delete",
    message: String = "Are you sure you want to delete this item? This action cannot be undone."
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AppText(text = title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            AppText(text = message)
        },
        confirmButton = {
            AppButton(
                text = "Delete",
                onClick = onConfirm,
                type = ButtonType.DANGER
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AppText("Cancel")
            }
        }
    )
}
