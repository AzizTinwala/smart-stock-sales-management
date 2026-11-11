package com.saifee.inventorysalesmanagement.mvvm.presentation.vendors.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppEmptyState
import com.saifee.inventorysalesmanagement.core.ui.components.AppFloatingActionButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.presentation.vendors.viewmodel.VendorsViewModel
import com.saifee.inventorysalesmanagement.mvvm.presentation.vendors.components.AddVendorBottomSheet
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Storefront
import com.saifee.inventorysalesmanagement.core.ui.components.AppSwitch
import com.saifee.inventorysalesmanagement.mvvm.presentation.vendors.viewmodel.VendorUiState
import com.saifee.inventorysalesmanagement.mvvm.presentation.common.components.DeleteConfirmationDialog

import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsScreen(
    viewModel: VendorsViewModel,
    onMenuClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    VendorsScreenContent(
        state = state,
        onMenuClick = onMenuClick,
        onAddVendorClick = { viewModel.setShowAddEditDialog(true) },
        onEditVendorClick = { viewModel.setShowAddEditDialog(true, it) },
        onDeleteVendorClick = { viewModel.deleteVendor(it) },
        onRestoreVendorClick = { viewModel.setShowAddEditDialog(true, it.copy(isDeleted = false)) },
        onNameChange = viewModel::onNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onEmailChange = viewModel::onEmailChange,
        onAddressChange = viewModel::onAddressChange,
        onBillingAddressChange = viewModel::onBillingAddressChange,
        onStateChange = viewModel::onStateChange,
        onOpeningBalanceChange = viewModel::onOpeningBalanceChange,
        onGstNumberChange = viewModel::onGstNumberChange,
        onTaxTypeChange = viewModel::onTaxTypeChange,
        onDismissDialog = { viewModel.setShowAddEditDialog(false) },
        onSaveVendor = { viewModel.saveVendor {} }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsScreenContent(
    state: VendorUiState,
    onMenuClick: () -> Unit,
    onAddVendorClick: () -> Unit,
    onEditVendorClick: (VendorEntity) -> Unit,
    onDeleteVendorClick: (VendorEntity) -> Unit,
    onRestoreVendorClick: (VendorEntity) -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onBillingAddressChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onOpeningBalanceChange: (String) -> Unit,
    onGstNumberChange: (String) -> Unit,
    onTaxTypeChange: (com.saifee.inventorysalesmanagement.core.enums.TaxType) -> Unit,
    onDismissDialog: () -> Unit,
    onSaveVendor: () -> Unit
) {
    var vendorToDelete by remember { mutableStateOf<VendorEntity?>(null) }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Vendors",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(onClick = onAddVendorClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Vendor")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.vendors.isEmpty()) {
            AppEmptyState(
                icon = Icons.Outlined.Storefront,
                title = "No Vendors",
                description = "Manage your suppliers and vendors here",
                actionText = "Add Vendor",
                onActionClick = onAddVendorClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppTokens.Spacing.Medium)
            ) {
                items(state.vendors) { vendor ->
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTokens.Spacing.Small)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AppText(text = vendor.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    if (vendor.isDeleted) {
                                        Spacer(Modifier.width(8.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = CircleShape
                                        ) {
                                            AppText(
                                                text = "Inactive",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                if (!vendor.gstNumber.isNullOrBlank()) {
                                    AppText(text = "GST: ${vendor.gstNumber} (${vendor.taxType})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                AppText(text = vendor.phone ?: "No Phone", style = MaterialTheme.typography.bodyMedium)
                                AppText(text = vendor.address ?: "No Address", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(
                                        text = { AppText("Edit") },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                        onClick = {
                                            expanded = false
                                            onEditVendorClick(vendor)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { AppText(if (vendor.isDeleted) "Restore" else "Delete", color = if (vendor.isDeleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) },
                                        leadingIcon = { 
                                            Icon(
                                                if (vendor.isDeleted) Icons.Default.Refresh else Icons.Default.Delete, 
                                                contentDescription = null, 
                                                tint = if (vendor.isDeleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            ) 
                                        },
                                        onClick = {
                                            expanded = false
                                            if (vendor.isDeleted) {
                                                onRestoreVendorClick(vendor)
                                            } else {
                                                vendorToDelete = vendor
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.showAddEditDialog) {
            AddVendorBottomSheet(
                name = state.name,
                phone = state.phone,
                email = state.email,
                address = state.address,
                billingAddress = state.billingAddress,
                state = state.state,
                openingBalance = state.openingBalance,
                gstNumber = state.gstNumber,
                taxType = state.taxType,
                isSaving = state.isSaving,
                errorMessage = state.errorMessage,
                isEditMode = state.isEditMode,
                onNameChange = onNameChange,
                onPhoneChange = onPhoneChange,
                onEmailChange = onEmailChange,
                onAddressChange = onAddressChange,
                onBillingAddressChange = onBillingAddressChange,
                onStateChange = onStateChange,
                onOpeningBalanceChange = onOpeningBalanceChange,
                onGstNumberChange = onGstNumberChange,
                onTaxTypeChange = onTaxTypeChange,
                onDismiss = onDismissDialog,
                onSave = onSaveVendor
            )
        }

        vendorToDelete?.let { vendor ->
            DeleteConfirmationDialog(
                onConfirm = {
                    onDeleteVendorClick(vendor)
                    vendorToDelete = null
                },
                onDismiss = { vendorToDelete = null },
                message = "Are you sure you want to delete vendor '${vendor.name}'?"
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VendorsScreenPreview() {
    InventorySalesManagementTheme {
        VendorsScreenContent(
            state = VendorUiState(
                vendors = listOf(
                    VendorEntity(
                        vendorId = "V1",
                        companyId = "CMP1",
                        locationId = "LOC1",
                        name = "Supplier A",
                        phone = "1112223333",
                        email = "supplier@example.com",
                        address = "Industrial Area",
                        taxType = "NONE",
                        createdAt = 0L,
                        updatedAt = 0L,
                        createdBy = "user",
                        updatedBy = "user"
                    ),
                    VendorEntity(
                        vendorId = "V2",
                        companyId = "CMP1",
                        locationId = "LOC1",
                        name = "Supplier B",
                        phone = "4445556666",
                        email = "supplierB@example.com",
                        address = "Market Square",
                        taxType = "NONE",
                        isDeleted = true,
                        createdAt = 0L,
                        updatedAt = 0L,
                        createdBy = "user",
                        updatedBy = "user"
                    )
                )
            ),
            onMenuClick = {},
            onAddVendorClick = {},
            onEditVendorClick = {},
            onDeleteVendorClick = {},
            onRestoreVendorClick = {},
            onNameChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onAddressChange = {},
            onBillingAddressChange = {},
            onStateChange = {},
            onOpeningBalanceChange = {},
            onGstNumberChange = {},
            onTaxTypeChange = {},
            onDismissDialog = {},
            onSaveVendor = {}
        )
    }
}
