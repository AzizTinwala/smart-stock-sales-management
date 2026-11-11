package com.saifee.inventorysalesmanagement.mvvm.presentation.payments.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import java.util.Locale
import androidx.compose.ui.graphics.Color
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
import com.saifee.inventorysalesmanagement.core.enums.PaymentMode
import com.saifee.inventorysalesmanagement.core.enums.PaymentType
import com.saifee.inventorysalesmanagement.core.ui.components.AppAutoCompleteTextView
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentBottomSheet(
    customers: List<CustomerEntity>,
    vendors: List<VendorEntity>,
    onDismiss: () -> Unit,
    onSave: (PaymentType, String, Double, PaymentMode, String?) -> Unit
) {
    var paymentType by remember { mutableStateOf(PaymentType.OUT) } // Default to Payment Received (Inflow)
    var selectedPartyId by remember { mutableStateOf("") }
    var partyName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var reference by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val partyList = if (paymentType == PaymentType.OUT) {
        customers.map { it.customerId to "${it.name} (Customer • Bal: ₹${String.format(Locale.getDefault(), "%.2f", it.openingBalance)})" }
    } else {
        vendors.map { it.vendorId to "${it.name} (Vendor • Bal: ₹${String.format(Locale.getDefault(), "%.2f", it.openingBalance)})" }
    }

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
                text = "Record Financial Transaction",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            AppText(
                text = "Log settlement payment and adjust party ledger balance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Payment Flow Direction Toggle
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AppText("Transaction Direction *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            paymentType = PaymentType.OUT
                            selectedPartyId = ""
                            partyName = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (paymentType == PaymentType.OUT) Color(0xFFE8F8EC) else MaterialTheme.colorScheme.surface,
                            contentColor = if (paymentType == PaymentType.OUT) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(1.dp, if (paymentType == PaymentType.OUT) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        AppText("Payment Received (Inflow)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            paymentType = PaymentType.IN
                            selectedPartyId = ""
                            partyName = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (paymentType == PaymentType.IN) Color(0xFFFEEBEE) else MaterialTheme.colorScheme.surface,
                            contentColor = if (paymentType == PaymentType.IN) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(1.dp, if (paymentType == PaymentType.IN) Color(0xFFC62828) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        AppText("Payment Made (Outflow)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Party Selection
            AppAutoCompleteTextView(
                label = if (paymentType == PaymentType.OUT) "Party / Customer *" else "Party / Vendor *",
                value = partyName,
                items = partyList.map { it.second },
                onItemSelected = { name ->
                    partyName = name
                    selectedPartyId = partyList.find { it.second == name }?.first ?: ""
                }
            )

            // Amount
            AppTextField(
                label = "Amount Paid (₹) *",
                value = amount,
                onValueChange = { amount = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = "0.00",
                modifier = Modifier.fillMaxWidth()
            )

            // Payment Mode Dropdown / Selection
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AppText("Payment Mode *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PaymentMode.entries.forEach { mode ->
                        val isSelected = paymentMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { paymentMode = mode },
                            label = { AppText(mode.label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Reference
            AppTextField(
                label = "Reference / UTR / Cheque #",
                value = reference,
                onValueChange = { reference = it },
                placeholder = "e.g. UTR-9821812",
                modifier = Modifier.fillMaxWidth()
            )

            // Remarks / Settlement Notes
            AppTextField(
                label = "Remarks / Settlement Notes",
                value = notes,
                onValueChange = { notes = it },
                placeholder = "e.g. Cleared pending invoices",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    AppText("Cancel")
                }
                
                AppButton(
                    text = "Confirm Payment",
                    onClick = {
                        val amt = amount.toDoubleOrNull() ?: 0.0
                        if (selectedPartyId.isNotEmpty() && amt > 0) {
                            val finalRef = if (reference.isBlank()) "DIRECT-${paymentMode.name}" else reference
                            val combinedNotes = notes.ifBlank { null }
                            onSave(paymentType, selectedPartyId, amt, paymentMode, finalRef)
                        }
                    },
                    enabled = selectedPartyId.isNotEmpty() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
