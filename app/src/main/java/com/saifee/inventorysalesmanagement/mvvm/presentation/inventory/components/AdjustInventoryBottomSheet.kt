package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustInventoryBottomSheet(
    item: ItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Double, reason: String, remark: String) -> Unit
) {
    var quantityStr by remember { mutableStateOf("") }
    var adjustmentType by remember { mutableStateOf("ADD") } // ADD or REMOVE
    var selectedReason by remember { mutableStateOf("Manual Adjustment") }
    var customReason by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }

    val reasons = listOf("Manual Adjustment", "Damage", "Return", "Stock Correction", "Other")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
        ) {
            AppText(
                text = "Adjust Inventory: ${item.itemName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            AppText(
                text = "Current Stock: ${item.stockQty} ${item.unit ?: ""}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
            ) {
                FilterChip(
                    selected = adjustmentType == "ADD",
                    onClick = { adjustmentType = "ADD" },
                    label = { AppText("Add Stock") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = adjustmentType == "REMOVE",
                    onClick = { adjustmentType = "REMOVE" },
                    label = { AppText("Remove Stock") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.error
                    )
                )
            }

            AppTextField(
                label = "Quantity to ${if (adjustmentType == "ADD") "Add" else "Remove"}",
                value = quantityStr,
                onValueChange = { quantityStr = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            AppText(text = "Reason", style = MaterialTheme.typography.labelMedium)
            Column(Modifier.selectableGroup()) {
                reasons.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedReason),
                                onClick = { selectedReason = text },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedReason),
                            onClick = null
                        )
                        AppText(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            if (selectedReason == "Other") {
                AppTextField(
                    label = "Specify Reason",
                    value = customReason,
                    onValueChange = { customReason = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AppTextField(
                label = "Remark / Notes (Optional)",
                value = remark,
                onValueChange = { remark = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

            AppButton(
                text = "Confirm Adjustment",
                onClick = {
                    val qty = quantityStr.toDoubleOrNull() ?: 0.0
                    if (qty > 0) {
                        val effectiveReason = if (selectedReason == "Other") {
                            customReason.ifBlank { "Other" }
                        } else {
                            selectedReason
                        }
                        onConfirm(
                            if (adjustmentType == "ADD") qty else -qty,
                            effectiveReason,
                            remark.trim()
                        )
                    }
                },
                enabled = quantityStr.toDoubleOrNull()?.let { it > 0 } ?: false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
