package com.saifee.inventorysalesmanagement.mvvm.presentation.vendors.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.TaxType
import com.saifee.inventorysalesmanagement.core.ui.components.AppAutoCompleteTextView
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVendorBottomSheet(
    name: String,
    phone: String,
    email: String,
    address: String,
    billingAddress: String,
    state: String,
    openingBalance: String,
    gstNumber: String,
    taxType: TaxType,
    isSaving: Boolean,
    errorMessage: String?,
    isEditMode: Boolean = false,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onBillingAddressChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onOpeningBalanceChange: (String) -> Unit,
    onGstNumberChange: (String) -> Unit,
    onTaxTypeChange: (TaxType) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small)
        ) {
            AppText(
                text = if (isEditMode) "Edit Vendor" else "Add Vendor",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            AppTextField(label = "Name", value = name, onValueChange = onNameChange, modifier = Modifier.fillMaxWidth())
            AppTextField(label = "Phone", value = phone, onValueChange = onPhoneChange, modifier = Modifier.fillMaxWidth())
            AppTextField(label = "Email", value = email, onValueChange = onEmailChange, modifier = Modifier.fillMaxWidth())
            AppTextField(label = "Address", value = address, onValueChange = onAddressChange, modifier = Modifier.fillMaxWidth())

            AppAutoCompleteTextView(
                label = "State",
                value = state,
                items = Constants.INDIAN_STATES,
                onItemSelected = onStateChange,
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(label = "Billing Address", value = billingAddress, onValueChange = onBillingAddressChange, modifier = Modifier.fillMaxWidth())
            AppTextField(label = "Opening Balance", value = openingBalance, onValueChange = onOpeningBalanceChange, modifier = Modifier.fillMaxWidth())

            // GST Section
            AppText(text = "Tax Type", style = MaterialTheme.typography.labelMedium)
            Column(Modifier.selectableGroup()) {
                TaxTypeOption(
                    label = "None",
                    selected = taxType == TaxType.NONE,
                    onClick = { onTaxTypeChange(TaxType.NONE) }
                )
                TaxTypeOption(
                    label = "CGST/SGST (Intra-state)",
                    selected = taxType == TaxType.INTRA_STATE,
                    onClick = { onTaxTypeChange(TaxType.INTRA_STATE) }
                )
                TaxTypeOption(
                    label = "IGST (Inter-state)",
                    selected = taxType == TaxType.INTER_STATE,
                    onClick = { onTaxTypeChange(TaxType.INTER_STATE) }
                )
            }

            if (taxType != TaxType.NONE) {
                AppTextField(
                    label = "GST Number",
                    value = gstNumber,
                    onValueChange = onGstNumberChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            errorMessage?.let {
                AppText(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

            AppButton(
                onClick = onSave,
                enabled = !isSaving,
                text = if (isEditMode) "Update Vendor" else "Add Vendor",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TaxTypeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        AppText(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
