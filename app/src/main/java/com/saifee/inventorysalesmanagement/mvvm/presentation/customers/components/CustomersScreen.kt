package com.saifee.inventorysalesmanagement.mvvm.presentation.customers.components

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
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.presentation.customers.viewmodel.CustomersViewModel

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.People
import com.saifee.inventorysalesmanagement.core.ui.components.AppSwitch
import com.saifee.inventorysalesmanagement.mvvm.presentation.customers.viewmodel.CustomerUiState
import com.saifee.inventorysalesmanagement.mvvm.presentation.common.components.DeleteConfirmationDialog

import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    viewModel: CustomersViewModel,
    onMenuClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    CustomersScreenContent(
        state = state,
        onMenuClick = onMenuClick,
        onAddCustomerClick = { viewModel.setShowAddEditDialog(true) },
        onEditCustomerClick = { viewModel.setShowAddEditDialog(true, it) },
        onDeleteCustomerClick = { viewModel.deleteCustomer(it) },
        onRestoreCustomerClick = { viewModel.setShowAddEditDialog(true, it.copy(isDeleted = false)) },
        onNameChange = viewModel::onNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onEmailChange = viewModel::onEmailChange,
        onAddressChange = viewModel::onAddressChange,
        onBillingAddressChange = viewModel::onBillingAddressChange,
        onShippingAddressChange = viewModel::onShippingAddressChange,
        onStateChange = viewModel::onStateChange,
        onOpeningBalanceChange = viewModel::onOpeningBalanceChange,
        onGstNumberChange = viewModel::onGstNumberChange,
        onTaxTypeChange = viewModel::onTaxTypeChange,
        onDismissDialog = { viewModel.setShowAddEditDialog(false) },
        onSaveCustomer = { viewModel.saveCustomer {} }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreenContent(
    state: CustomerUiState,
    onMenuClick: () -> Unit,
    onAddCustomerClick: () -> Unit,
    onEditCustomerClick: (CustomerEntity) -> Unit,
    onDeleteCustomerClick: (CustomerEntity) -> Unit,
    onRestoreCustomerClick: (CustomerEntity) -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onBillingAddressChange: (String) -> Unit,
    onShippingAddressChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onOpeningBalanceChange: (String) -> Unit,
    onGstNumberChange: (String) -> Unit,
    onTaxTypeChange: (com.saifee.inventorysalesmanagement.core.enums.TaxType) -> Unit,
    onDismissDialog: () -> Unit,
    onSaveCustomer: () -> Unit
) {
    var customerToDelete by remember { mutableStateOf<CustomerEntity?>(null) }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Customers",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(onClick = onAddCustomerClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.customers.isEmpty()) {
            AppEmptyState(
                icon = Icons.Outlined.People,
                title = "No Customers",
                description = "Keep track of your customer base here",
                actionText = "Add Customer",
                onActionClick = onAddCustomerClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppTokens.Spacing.Medium)
            ) {
                items(state.customers) { customer ->
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
                                    AppText(text = customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    if (customer.isDeleted) {
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
                                if (!customer.gstNumber.isNullOrBlank()) {
                                    AppText(text = "GST: ${customer.gstNumber} (${customer.taxType})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                AppText(text = customer.phone ?: "No Phone", style = MaterialTheme.typography.bodyMedium)
                                AppText(text = customer.email ?: "No Email", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                            onEditCustomerClick(customer)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { AppText(if (customer.isDeleted) "Restore" else "Delete", color = if (customer.isDeleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) },
                                        leadingIcon = { 
                                            Icon(
                                                if (customer.isDeleted) Icons.Default.Refresh else Icons.Default.Delete, 
                                                contentDescription = null, 
                                                tint = if (customer.isDeleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            ) 
                                        },
                                        onClick = {
                                            expanded = false
                                            if (customer.isDeleted) {
                                                onRestoreCustomerClick(customer)
                                            } else {
                                                customerToDelete = customer
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
            AddCustomerBottomSheet(
                name = state.name,
                phone = state.phone,
                email = state.email,
                address = state.address,
                billingAddress = state.billingAddress,
                shippingAddress = state.shippingAddress,
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
                onShippingAddressChange = onShippingAddressChange,
                onStateChange = onStateChange,
                onOpeningBalanceChange = onOpeningBalanceChange,
                onGstNumberChange = onGstNumberChange,
                onTaxTypeChange = onTaxTypeChange,
                onDismiss = onDismissDialog,
                onSave = onSaveCustomer
            )
        }

        customerToDelete?.let { customer ->
            DeleteConfirmationDialog(
                onConfirm = {
                    onDeleteCustomerClick(customer)
                    customerToDelete = null
                },
                onDismiss = { customerToDelete = null },
                message = "Are you sure you want to delete customer '${customer.name}'?"
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CustomersScreenPreview() {
    InventorySalesManagementTheme {
        CustomersScreenContent(
            state = CustomerUiState(
                customers = listOf(
                    CustomerEntity(
                        customerId = "C1",
                        companyId = "CMP1",
                        locationId = "LOC1",
                        name = "John Doe",
                        phone = "1234567890",
                        email = "john@example.com",
                        address = "Sample Address",
                        taxType = "NONE",
                        createdAt = 0L,
                        updatedAt = 0L
                    ),
                    CustomerEntity(
                        customerId = "C2",
                        companyId = "CMP1",
                        locationId = "LOC1",
                        name = "Jane Smith",
                        phone = "9876543210",
                        email = "jane@example.com",
                        address = "Sample Address 2",
                        taxType = "NONE",
                        isDeleted = true,
                        createdAt = 0L,
                        updatedAt = 0L
                    )
                )
            ),
            onMenuClick = {},
            onAddCustomerClick = {},
            onEditCustomerClick = {},
            onDeleteCustomerClick = {},
            onRestoreCustomerClick = {},
            onNameChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onAddressChange = {},
            onBillingAddressChange = {},
            onShippingAddressChange = {},
            onStateChange = {},
            onOpeningBalanceChange = {},
            onGstNumberChange = {},
            onTaxTypeChange = {},
            onDismissDialog = {},
            onSaveCustomer = {}
        )
    }
}
