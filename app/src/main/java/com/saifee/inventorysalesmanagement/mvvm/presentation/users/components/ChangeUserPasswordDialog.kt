package com.saifee.inventorysalesmanagement.mvvm.presentation.users.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppPasswordField
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile

@Composable
fun ChangeUserPasswordDialog(
    user: UserProfile,
    onDismiss: () -> Unit,
    onConfirmPasswordChange: (newPassword: String) -> Unit,
    onSendResetEmail: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMismatchError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                AppText(
                    text = "Change User Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                AppText(
                    text = "Updating credentials for ${user.name.ifEmpty { user.email }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppPasswordField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        passwordMismatchError = confirmPassword.isNotEmpty() && newPassword != confirmPassword
                    },
                    label = "New Password",
                    modifier = Modifier.fillMaxWidth()
                )

                AppPasswordField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        passwordMismatchError = newPassword.isNotEmpty() && newPassword != confirmPassword
                    },
                    label = "Confirm New Password",
                    modifier = Modifier.fillMaxWidth()
                )

                if (passwordMismatchError) {
                    AppText(
                        text = "Passwords do not match.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                AppText(
                    text = "Alternative Option:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = {
                        onSendResetEmail()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppText("Send Password Reset Email")
                }
            }
        },
        confirmButton = {
            AppButton(
                text = "Update Password",
                enabled = newPassword.length >= 6 && newPassword == confirmPassword,
                onClick = {
                    onConfirmPasswordChange(newPassword)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AppText("Cancel")
            }
        }
    )
}
