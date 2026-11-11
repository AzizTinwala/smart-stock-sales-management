package com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.TaxType
import com.saifee.inventorysalesmanagement.core.ui.components.AppAutoCompleteTextView
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppFormColumn
import com.saifee.inventorysalesmanagement.core.ui.components.AppSwitch
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.components.StatusBadge
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.theme.stockColors
import com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.state.CreatePurchaseUiState
import com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.viewmodel.CreatePurchaseViewModel
import com.saifee.inventorysalesmanagement.mvvm.presentation.vendors.components.AddVendorBottomSheet
import java.util.Locale

/**
 * Screen 5: Stock In / Receive Inventory Workflow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePurchaseScreen(
    viewModel: CreatePurchaseViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    CreatePurchaseScreenContent(
        state = state,
        isEditMode = viewModel.isEditMode,
        onBack = onBack,
        onCashPurchaseToggle = viewModel::onCashPurchaseToggle,
        onVendorSelected = viewModel::onVendorSelected,
        onAddVendorClick = { viewModel.setShowAddVendorDialog(true) },
        onAddItemToCart = { item -> viewModel.addItemToCart(item, 1.0, item.purchasePrice) },
        onRemoveCartItem = viewModel::removeCartItem,
        onUpdateCartItemPrice = viewModel::updateCartItemPrice,
        onUpdateCartItemQuantity = viewModel::updateCartItemQuantity,
        onSavePurchase = { viewModel.savePurchase(onBack) },
        onVendorNameChange = viewModel::onVendorNameChange,
        onVendorGstChange = viewModel::onVendorGstChange,
        onVendorTaxTypeChange = viewModel::onVendorTaxTypeChange,
        onVendorPhoneChange = viewModel::onVendorPhoneChange,
        onVendorEmailChange = viewModel::onVendorEmailChange,
        onVendorAddressChange = viewModel::onVendorAddressChange,
        onVendorBillingAddressChange = viewModel::onVendorBillingAddressChange,
        onVendorStateChange = viewModel::onVendorStateChange,
        onVendorOpeningBalanceChange = viewModel::onVendorOpeningBalanceChange,
        onDismissVendorDialog = { viewModel.setShowAddVendorDialog(false) },
        onSaveNewVendor = viewModel::saveNewVendor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePurchaseScreenContent(
    state: CreatePurchaseUiState,
    isEditMode: Boolean,
    onBack: () -> Unit,
    onCashPurchaseToggle: (Boolean) -> Unit,
    onVendorSelected: (com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity?) -> Unit,
    onAddVendorClick: () -> Unit,
    onAddItemToCart: (com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity) -> Unit,
    onRemoveCartItem: (com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.state.PurchaseCartItem) -> Unit,
    onUpdateCartItemPrice: (com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.state.PurchaseCartItem, Double) -> Unit,
    onUpdateCartItemQuantity: (com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.state.PurchaseCartItem, Double) -> Unit,
    onSavePurchase: () -> Unit,
    onVendorNameChange: (String) -> Unit,
    onVendorGstChange: (String) -> Unit,
    onVendorTaxTypeChange: (TaxType) -> Unit,
    onVendorPhoneChange: (String) -> Unit,
    onVendorEmailChange: (String) -> Unit,
    onVendorAddressChange: (String) -> Unit,
    onVendorBillingAddressChange: (String) -> Unit,
    onVendorStateChange: (String) -> Unit,
    onVendorOpeningBalanceChange: (String) -> Unit,
    onDismissVendorDialog: () -> Unit,
    onSaveNewVendor: () -> Unit
) {
    val stockColors = MaterialTheme.colorScheme.stockColors

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (isEditMode) "Edit Stock In" else "Stock In / Receive Inventory",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        }
    ) { padding ->
        AppFormColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small),
            verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
        ) {
            // Cash Purchase Toggle
            AppSwitch(
                title = "Direct Cash Purchase",
                checked = state.isCashPurchase,
                onCheckedChange = onCashPurchaseToggle
            )

            // Supplier / Vendor Selection
            if (!state.isCashPurchase) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small)
                ) {
                    AppAutoCompleteTextView(
                        modifier = Modifier.weight(1f),
                        label = "Select Supplier / Vendor",
                        value = state.selectedVendor?.name ?: "",
                        items = state.vendors.map { it.name },
                        onItemSelected = { name ->
                            val vendor = state.vendors.find { it.name == name }
                            onVendorSelected(vendor)
                        }
                    )

                    IconButton(onClick = onAddVendorClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add Supplier", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            // Product Selection
            var selectedItemName by remember { mutableStateOf("") }
            AppAutoCompleteTextView(
                label = "Search & Select Product to Receive",
                value = selectedItemName,
                items = state.items.map { "${it.itemName} (${it.stockQty.toInt()} in stock)" },
                onItemSelected = { displayName ->
                    val item = state.items.find { "${it.itemName} (${it.stockQty.toInt()} in stock)" == displayName }
                    item?.let { onAddItemToCart(it) }
                    selectedItemName = ""
                }
            )

            Text("Stock In Items (${state.cart.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.cart) { cartItem ->
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTokens.Spacing.ExtraSmall)
                    ) {
                        Column(modifier = Modifier.padding(AppTokens.Spacing.Small)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        cartItem.item.itemName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    StatusBadge(
                                        label = "Current Stock: ${cartItem.item.stockQty.toInt()} ${cartItem.item.unit.orEmpty().ifEmpty { "pcs" }}",
                                        containerColor = stockColors.inStockContainer,
                                        contentColor = stockColors.onInStockContainer
                                    )
                                }
                                IconButton(onClick = { onRemoveCartItem(cartItem) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppTextField(
                                    modifier = Modifier.weight(1f),
                                    label = "Cost Price ($)",
                                    value = cartItem.costPrice.toString(),
                                    onValueChange = { 
                                        it.toDoubleOrNull()?.let { p -> onUpdateCartItemPrice(cartItem, p) } 
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                AppTextField(
                                    modifier = Modifier.weight(0.7f),
                                    label = "Receive Qty",
                                    value = cartItem.quantity.toString(),
                                    onValueChange = { 
                                        it.toDoubleOrNull()?.let { q -> onUpdateCartItemQuantity(cartItem, q) } 
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                Text(
                                    text = "$${String.format(Locale.getDefault(), "%.2f", cartItem.total)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Total Purchase Value",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "$${String.format(Locale.getDefault(), "%.2f", state.finalAmount)}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (state.error != null) {
                Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            AppButton(
                text = if (isEditMode) "Update Stock In" else "Confirm Stock In",
                enabled = state.cart.isNotEmpty(),
                isLoading = state.isSaving,
                onClick = onSavePurchase
            )
        }
    }

    if (state.showAddVendorDialog) {
        AddVendorBottomSheet(
            name = state.vendorName,
            gstNumber = state.gstNumber,
            taxType = state.taxType,
            phone = state.vendorPhone,
            email = state.vendorEmail,
            address = state.vendorAddress,
            billingAddress = state.billingAddress,
            state = state.state,
            openingBalance = state.openingBalance,
            isSaving = state.isSavingVendor,
            errorMessage = state.vendorError,
            onNameChange = onVendorNameChange,
            onGstNumberChange = onVendorGstChange,
            onTaxTypeChange = onVendorTaxTypeChange,
            onPhoneChange = onVendorPhoneChange,
            onEmailChange = onVendorEmailChange,
            onAddressChange = onVendorAddressChange,
            onBillingAddressChange = onVendorBillingAddressChange,
            onStateChange = onVendorStateChange,
            onOpeningBalanceChange = onVendorOpeningBalanceChange,
            onDismiss = onDismissVendorDialog,
            onSave = onSaveNewVendor
        )
    }
}
