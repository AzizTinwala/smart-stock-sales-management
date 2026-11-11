package com.saifee.inventorysalesmanagement.mvvm.presentation.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.ButtonType
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun ManagerPinDialog(
    onConfirmPin: (String) -> Unit,
    onDismiss: () -> Unit,
    title: String = "Manager Authorization Required",
    message: String = "This action requires Manager approval. Please enter a valid Manager PIN.",
    errorMessage: String? = null,
    isLoading: Boolean = false
) {
    var pinText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AppText(text = title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column {
                AppText(text = message, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = pinText,
                    onValueChange = { if (it.length <= 6) pinText = it },
                    label = { AppText("Manager PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppText(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            AppButton(
                text = if (isLoading) "Verifying..." else "Authorize",
                onClick = {
                    if (pinText.isNotEmpty()) {
                        onConfirmPin(pinText)
                    }
                },
                enabled = pinText.length >= 4 && !isLoading,
                type = ButtonType.PRIMARY
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                AppText("Cancel")
            }
        }
    )
}
