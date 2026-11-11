package com.saifee.inventorysalesmanagement.mvvm.presentation.users.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile

@Composable
fun FeaturePermissionsDialog(
    user: UserProfile,
    onDismiss: () -> Unit,
    onSubmit: (permissions: Map<String, Boolean>) -> Unit
) {
    val featureList = listOf(
        "sales" to "Sales & Invoicing",
        "purchases" to "Purchases & Orders",
        "inventory" to "Inventory Management",
        "items" to "Items & Item Groups",
        "customers" to "Customer Directory",
        "vendors" to "Vendor Directory",
        "payments" to "Payments In/Out",
        "ledger" to "Party Ledger",
        "reports" to "Reports & Analytics"
    )

    val permissionsState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            featureList.forEach { (key, _) ->
                put(key, user.featurePermissions[key] ?: true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                AppText(
                    text = "Feature Permissions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                AppText(
                    text = "Control access for ${user.name.ifEmpty { user.email }}",
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                featureList.forEachIndexed { index, (key, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = permissionsState[key] ?: true,
                            onCheckedChange = { isChecked ->
                                permissionsState[key] = isChecked
                            }
                        )
                    }

                    if (index < featureList.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        },
        confirmButton = {
            AppButton(
                text = "Save Permissions",
                onClick = {
                    onSubmit(permissionsState.toMap())
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
