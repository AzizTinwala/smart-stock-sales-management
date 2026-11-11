package com.saifee.inventorysalesmanagement.mvvm.presentation.sales.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.saifee.inventorysalesmanagement.mvvm.presentation.customers.components.AddCustomerBottomSheet
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.state.CreateSaleUiState
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.viewmodel.CreateSaleViewModel
import java.util.Locale

/**
 * Screen 6: Stock Out / Issue Inventory Workflow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSaleScreen(
    viewModel: CreateSaleViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    CreateSaleScreenContent(
        state = state,
        isEditMode = viewModel.isEditMode,
        onBack = onBack,
        onCashSaleToggle = viewModel::onCashSaleToggle,
        onCustomerSelected = viewModel::onCustomerSelected,
        onAddCustomerClick = { viewModel.setShowAddCustomerDialog(true) },
        onTaxTypeToggle = viewModel::onTaxTypeToggle,
        onAddItemToCart = { item -> viewModel.addItemToCart(item, 1.0) },
        onRemoveCartItem = viewModel::removeCartItem,
        onUpdateCartItemPrice = viewModel::updateCartItemPrice,
        onUpdateCartItemQuantity = viewModel::updateCartItemQuantity,
        onDiscountChange = viewModel::onDiscountChange,
        onRoundOffToggle = viewModel::onRoundOffToggle,
        onSaveSale = { viewModel.saveSale(onBack) },
        onCustomerNameChange = viewModel::onCustomerNameChange,
        onCustomerGstChange = viewModel::onCustomerGstChange,
        onCustomerTaxTypeChange = viewModel::onCustomerTaxTypeChange,
        onCustomerPhoneChange = viewModel::onCustomerPhoneChange,
        onCustomerEmailChange = viewModel::onCustomerEmailChange,
        onCustomerAddressChange = viewModel::onCustomerAddressChange,
        onCustomerBillingAddressChange = viewModel::onCustomerBillingAddressChange,
        onCustomerShippingAddressChange = viewModel::onCustomerShippingAddressChange,
        onCustomerStateChange = viewModel::onCustomerStateChange,
        onCustomerOpeningBalanceChange = viewModel::onCustomerOpeningBalanceChange,
        onDismissCustomerDialog = { viewModel.setShowAddCustomerDialog(false) },
        onSaveNewCustomer = viewModel::saveNewCustomer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSaleScreenContent(
    state: CreateSaleUiState,
    isEditMode: Boolean,
    onBack: () -> Unit,
    onCashSaleToggle: (Boolean) -> Unit,
    onCustomerSelected: (com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity?) -> Unit,
    onAddCustomerClick: () -> Unit,
    onTaxTypeToggle: (TaxType) -> Unit,
    onAddItemToCart: (com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity) -> Unit,
    onRemoveCartItem: (com.saifee.inventorysalesmanagement.mvvm.presentation.sales.state.CartItem) -> Unit,
    onUpdateCartItemPrice: (com.saifee.inventorysalesmanagement.mvvm.presentation.sales.state.CartItem, Double) -> Unit,
    onUpdateCartItemQuantity: (com.saifee.inventorysalesmanagement.mvvm.presentation.sales.state.CartItem, Double) -> Unit,
    onDiscountChange: (String) -> Unit,
    onRoundOffToggle: (Boolean) -> Unit,
    onSaveSale: () -> Unit,
    onCustomerNameChange: (String) -> Unit,
    onCustomerGstChange: (String) -> Unit,
    onCustomerTaxTypeChange: (TaxType) -> Unit,
    onCustomerPhoneChange: (String) -> Unit,
    onCustomerEmailChange: (String) -> Unit,
    onCustomerAddressChange: (String) -> Unit,
    onCustomerBillingAddressChange: (String) -> Unit,
    onCustomerShippingAddressChange: (String) -> Unit,
    onCustomerStateChange: (String) -> Unit,
    onCustomerOpeningBalanceChange: (String) -> Unit,
    onDismissCustomerDialog: () -> Unit,
    onSaveNewCustomer: () -> Unit
) {
    val stockColors = MaterialTheme.colorScheme.stockColors
    val overIssuedCartItem = state.cart.find { it.quantity > it.item.stockQty }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (isEditMode) "Edit Stock Out" else "Stock Out / Issue Inventory",
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
            // Cash Sale Toggle
            AppSwitch(
                title = "Direct Cash Issue",
                checked = state.isCashSale,
                onCheckedChange = onCashSaleToggle
            )

            // Customer / Party Selection
            if (!state.isCashSale) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small)
                ) {
                    AppAutoCompleteTextView(
                        modifier = Modifier.weight(1f),
                        label = "Select Customer / Destination Party",
                        value = state.selectedCustomer?.name ?: "",
                        items = state.customers.map { it.name },
                        onItemSelected = { name ->
                            val customer = state.customers.find { it.name == name }
                            onCustomerSelected(customer)
                        }
                    )

                    IconButton(onClick = onAddCustomerClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add Customer", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Item Selection
            var selectedItemName by remember { mutableStateOf("") }
            AppAutoCompleteTextView(
                label = "Search & Select Product to Issue",
                value = selectedItemName,
                items = state.items.map { "${it.itemName} (${it.stockQty.toInt()} in stock)" },
                onItemSelected = { displayName ->
                    val item = state.items.find { "${it.itemName} (${it.stockQty.toInt()} in stock)" == displayName }
                    item?.let { onAddItemToCart(it) }
                    selectedItemName = ""
                }
            )

            // Over-issue Error Banner
            if (overIssuedCartItem != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = stockColors.outOfStockContainer, shape = RoundedCornerShape(10.dp))
                        .border(width = 1.dp, color = stockColors.outOfStock, shape = RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = stockColors.outOfStock,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Over-issue Lock: '${overIssuedCartItem.item.itemName}' requested (${overIssuedCartItem.quantity.toInt()}) exceeds stock (${overIssuedCartItem.item.stockQty.toInt()}).",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = stockColors.onOutOfStockContainer
                    )
                }
            }

            Text("Products to Issue (${state.cart.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.cart) { cartItem ->
                    val isExceeded = cartItem.quantity > cartItem.item.stockQty

                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTokens.Spacing.ExtraSmall)
                            .border(
                                width = if (isExceeded) 1.dp else AppTokens.Dimensions.BorderWidth,
                                color = if (isExceeded) stockColors.outOfStock else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(AppTokens.CornerRadius.Card)
                            )
                    ) {
                        Column(modifier = Modifier.padding(AppTokens.Spacing.Small)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cartItem.item.itemName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    StatusBadge(
                                        label = "Available Stock: ${cartItem.item.stockQty.toInt()} ${cartItem.item.unit.orEmpty().ifEmpty { "pcs" }}",
                                        containerColor = if (isExceeded) stockColors.outOfStockContainer else stockColors.inStockContainer,
                                        contentColor = if (isExceeded) stockColors.onOutOfStockContainer else stockColors.onInStockContainer
                                    )
                                }
                                IconButton(onClick = { onRemoveCartItem(cartItem) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppTextField(
                                    modifier = Modifier.weight(1f),
                                    label = "Selling Price ($)",
                                    value = cartItem.price.toString(),
                                    onValueChange = { 
                                        it.toDoubleOrNull()?.let { p -> onUpdateCartItemPrice(cartItem, p) } 
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                AppTextField(
                                    modifier = Modifier.weight(0.7f),
                                    label = "Issue Qty",
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
                    "Total Valuation",
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
                text = if (isEditMode) "Update Stock Out" else "Confirm Stock Out",
                enabled = overIssuedCartItem == null && state.cart.isNotEmpty(),
                isLoading = state.isSaving,
                onClick = onSaveSale
            )
        }
    }

    if (state.showAddCustomerDialog) {
        AddCustomerBottomSheet(
            name = state.customerName,
            gstNumber = state.gstNumber,
            taxType = state.quickTaxType,
            phone = state.customerPhone,
            email = state.customerEmail,
            address = state.customerAddress,
            billingAddress = state.billingAddress,
            shippingAddress = state.shippingAddress,
            state = state.state,
            openingBalance = state.openingBalance,
            isSaving = state.isSavingCustomer,
            errorMessage = state.customerError,
            onNameChange = onCustomerNameChange,
            onGstNumberChange = onCustomerGstChange,
            onTaxTypeChange = onCustomerTaxTypeChange,
            onPhoneChange = onCustomerPhoneChange,
            onEmailChange = onCustomerEmailChange,
            onAddressChange = onCustomerAddressChange,
            onBillingAddressChange = onCustomerBillingAddressChange,
            onShippingAddressChange = onCustomerShippingAddressChange,
            onStateChange = onCustomerStateChange,
            onOpeningBalanceChange = onCustomerOpeningBalanceChange,
            onDismiss = onDismissCustomerDialog,
            onSave = onSaveNewCustomer
        )
    }
}
