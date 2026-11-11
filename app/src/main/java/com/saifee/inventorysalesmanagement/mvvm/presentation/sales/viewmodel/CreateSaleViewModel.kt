package com.saifee.inventorysalesmanagement.mvvm.presentation.sales.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.core.enums.TaxType
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.AddCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.AddSaleUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.UpdateSaleUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.CustomerUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.SalesUseCases
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.state.CartItem
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.state.CreateSaleUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateSaleViewModel @Inject constructor(
    private val salesUseCases: SalesUseCases,
    private val itemUseCases: ItemUseCases,
    private val customerUseCases: CustomerUseCases,
    private val tenantRepository: TenantRepository,
    private val sharedPref: SharedPref,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateSaleUiState(
        isRoundOffEnabled = sharedPref.defaultRoundOff
    ))
    val uiState: StateFlow<CreateSaleUiState> = _uiState.asStateFlow()

    // Decoupled search query state flow backed by SavedStateHandle to prevent recomposition storms
    val searchQuery: StateFlow<String> = savedStateHandle.getStateFlow("searchQuery", "")

    fun setSearchQuery(query: String) {
        savedStateHandle["searchQuery"] = query
    }

    private val salesId: String? = savedStateHandle["salesId"]
    val isEditMode = salesId != null
    private var existingSale: SalesEntity? = null

    init {
        loadInitialData()
        salesId?.let { loadSale(it) }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Load Customers
            launch {
                when (val customersResult = customerUseCases.getAll(Unit)) {
                    is ResultState.Success -> {
                        customersResult.data.collectLatest { list ->
                            _uiState.update { it.copy(customers = list.filter { !it.isDeleted || it.customerId == existingSale?.customerId }) }
                        }
                    }
                    else -> {}
                }
            }

            // Load Items
            launch {
                when (val itemsResult = itemUseCases.getAll(Unit)) {
                    is ResultState.Success -> {
                        itemsResult.data.collectLatest { list ->
                            _uiState.update { it.copy(items = list, isLoading = false) }
                        }
                    }
                    else -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    private fun loadSale(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = salesUseCases.getById(id)) {
                is ResultState.Success -> {
                    val salesWithItems = result.data
                    existingSale = salesWithItems?.sales
                    
                    salesWithItems?.let { data ->
                        val cartItems = data.items.mapNotNull { saleItem ->
                            val item = uiState.value.items.find { it.itemId == saleItem.itemId }
                            item?.let { CartItem(it, saleItem.quantity, saleItem.price) }
                        }
                        val isCash = data.sales.customerId == "CASH"
                        
                        _uiState.update { state ->
                            state.copy(
                                selectedCustomer = state.customers.find { it.customerId == data.sales.customerId },
                                isCashSale = isCash,
                                cart = cartItems,
                                discount = data.sales.discount,
                                taxType = if (isCash) TaxType.INTRA_STATE else state.taxType,
                                taxRate = if (data.sales.totalAmount != 0.0) data.sales.tax / data.sales.totalAmount else 0.18,
                                isLoading = false
                            )
                        }
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onCustomerSelected(customer: CustomerEntity?) {
        val isCash = customer == null
        _uiState.update { 
            it.copy(
                selectedCustomer = customer, 
                isCashSale = isCash,
                taxType = if (isCash) TaxType.INTRA_STATE else it.taxType
            ) 
        }
    }

    fun onCashSaleToggle(isCash: Boolean) {
        _uiState.update { 
            it.copy(
                isCashSale = isCash,
                selectedCustomer = if (isCash) null else it.selectedCustomer,
                taxType = if (isCash) TaxType.INTRA_STATE else it.taxType
            )
        }
    }

    fun onTaxTypeToggle(taxType: TaxType) {
        if (uiState.value.isCashSale && taxType == TaxType.INTER_STATE) return
        _uiState.update { it.copy(taxType = taxType) }
    }

    fun onRoundOffToggle(enabled: Boolean) {
        _uiState.update { it.copy(isRoundOffEnabled = enabled) }
    }

    fun onDiscountChange(discount: String) {
        _uiState.update { it.copy(discount = discount.toDoubleOrNull() ?: 0.0) }
    }

    fun addItemToCart(item: ItemEntity, quantity: Double) {
        val validQty = if (quantity <= 0.0) 1.0 else quantity
        _uiState.update { state ->
            val existing = state.cart.find { it.item.itemId == item.itemId }
            val newCart = if (existing != null) {
                state.cart.map { 
                    if (it.item.itemId == item.itemId) it.copy(quantity = it.quantity + validQty) 
                    else it 
                }
            } else {
                state.cart + CartItem(item, validQty, item.sellingPrice)
            }
            state.copy(cart = newCart)
        }
    }

    fun updateCartItemPrice(cartItem: CartItem, newPrice: Double) {
        val validPrice = if (newPrice < 0.0 || newPrice.isNaN() || newPrice.isInfinite()) 0.0 else newPrice
        _uiState.update { state ->
            val newCart = state.cart.map { 
                if (it.item.itemId == cartItem.item.itemId) it.copy(price = validPrice) 
                else it 
            }
            state.copy(cart = newCart)
        }
    }

    fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Double) {
        val validQty = if (newQuantity <= 0.0 || newQuantity.isNaN() || newQuantity.isInfinite()) 1.0 else newQuantity
        _uiState.update { state ->
            val newCart = state.cart.map { 
                if (it.item.itemId == cartItem.item.itemId) it.copy(quantity = validQty) 
                else it 
            }
            state.copy(cart = newCart)
        }
    }

    fun removeCartItem(cartItem: CartItem) {
        _uiState.update { state ->
            state.copy(cart = state.cart - cartItem)
        }
    }

    fun setShowAddCustomerDialog(show: Boolean) {
        _uiState.update { it.copy(
            showAddCustomerDialog = show,
            customerName = "",
            customerPhone = "",
            customerEmail = "",
            customerAddress = "",
            billingAddress = "",
            shippingAddress = "",
            state = "",
            openingBalance = "",
            gstNumber = "",
            quickTaxType = TaxType.NONE,
            customerError = null
        ) }
    }

    fun onCustomerNameChange(value: String) {
        _uiState.update { it.copy(customerName = value) }
    }

    fun onCustomerPhoneChange(value: String) {
        _uiState.update { it.copy(customerPhone = value) }
    }

    fun onCustomerEmailChange(value: String) {
        _uiState.update { it.copy(customerEmail = value) }
    }

    fun onCustomerAddressChange(value: String) {
        _uiState.update { it.copy(customerAddress = value) }
    }

    fun onCustomerBillingAddressChange(value: String) {
        _uiState.update { it.copy(billingAddress = value) }
    }

    fun onCustomerShippingAddressChange(value: String) {
        _uiState.update { it.copy(shippingAddress = value) }
    }

    fun onCustomerStateChange(value: String) {
        _uiState.update { it.copy(state = value) }
    }

    fun onCustomerOpeningBalanceChange(value: String) {
        _uiState.update { it.copy(openingBalance = value) }
    }

    fun onCustomerGstChange(value: String) {
        _uiState.update { it.copy(gstNumber = value) }
    }

    fun onCustomerTaxTypeChange(value: TaxType) {
        _uiState.update { it.copy(quickTaxType = value) }
    }

    fun saveNewCustomer() {
        val state = uiState.value
        if (state.customerName.isBlank()) {
            _uiState.update { it.copy(customerError = "Name is required") }
            return
        }
        if (state.quickTaxType != TaxType.NONE && state.gstNumber.isBlank()) {
            _uiState.update { it.copy(customerError = "GST Number is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingCustomer = true) }
            val newCustomer = CustomerEntity(
                customerId = UUID.randomUUID().toString(),
                companyId = "",
                locationId = "",
                name = state.customerName,
                phone = state.customerPhone.ifBlank { null },
                email = state.customerEmail.ifBlank { null },
                address = state.customerAddress.ifBlank { null },
                billingAddress = state.billingAddress.ifBlank { null },
                shippingAddress = state.shippingAddress.ifBlank { null },
                state = state.state.ifBlank { null },
                openingBalance = state.openingBalance.toDoubleOrNull() ?: 0.0,
                gstNumber = if (state.quickTaxType != TaxType.NONE) state.gstNumber.ifBlank { null } else null,
                taxType = state.quickTaxType.name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val result = customerUseCases.add(AddCustomerUseCase.AddCustomerParams(newCustomer))
            when (result) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(
                        isSavingCustomer = false,
                        showAddCustomerDialog = false,
                        selectedCustomer = newCustomer,
                        isCashSale = false
                    ) }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isSavingCustomer = false, customerError = result.message) }
                }
                else -> {}
            }
        }
    }

    fun saveSale(onSuccess: () -> Unit) {
        val state = uiState.value
        if (state.cart.isEmpty()) {
            _uiState.update { it.copy(error = "Cart is empty") }
            return
        }
        if (state.cart.any { it.quantity <= 0.0 || it.price < 0.0 }) {
            _uiState.update { it.copy(error = "Cart contains invalid item quantities or prices") }
            return
        }
        if (!state.isCashSale && state.selectedCustomer == null) {
            _uiState.update { it.copy(error = "Please select a customer or toggle Cash Sale") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val currentSalesId = salesId ?: UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val uniqueSuffix = UUID.randomUUID().toString().take(6).uppercase()
            
            val sale = SalesEntity(
                salesId = currentSalesId,
                companyId = tenantRepository.getSelectedCompanyId(),
                locationId = tenantRepository.getSelectedLocationId(),
                invoiceNumber = existingSale?.invoiceNumber ?: "INV-${now.toString().takeLast(6)}-$uniqueSuffix",
                customerId = if (state.isCashSale) "CASH" else state.selectedCustomer?.customerId,
                totalAmount = state.subTotal,
                discount = state.discount,
                tax = state.taxAmount,
                finalAmount = state.finalAmount,
                status = "PAID",
                createdAt = existingSale?.createdAt ?: now,
                updatedAt = now
            )
            
            val salesItems = state.cart.map { cartItem ->
                SalesItemEntity(
                    salesItemId = UUID.randomUUID().toString(),
                    companyId = sale.companyId,
                    locationId = sale.locationId,
                    salesId = currentSalesId,
                    itemId = cartItem.item.itemId,
                    quantity = cartItem.quantity,
                    price = cartItem.price,
                    total = cartItem.total,
                    createdAt = now,
                    updatedAt = now
                )
            }

            val result = if (isEditMode) {
                salesUseCases.update(UpdateSaleUseCase.UpdateSaleParams(sale, salesItems))
            } else {
                salesUseCases.add(AddSaleUseCase.AddSalesParam(sale, salesItems))
            }
            
            when (result) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                else -> {}
            }
        }
    }
}
