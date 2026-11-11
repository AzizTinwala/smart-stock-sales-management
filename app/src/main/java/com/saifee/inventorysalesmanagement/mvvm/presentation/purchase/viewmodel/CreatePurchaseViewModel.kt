package com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.core.enums.TaxType
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.AddPurchaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.UpdatePurchaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.AddVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.PurchaseUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.VendorUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.state.CreatePurchaseUiState
import com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.state.PurchaseCartItem
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
class CreatePurchaseViewModel @Inject constructor(
    private val purchaseUseCases: PurchaseUseCases,
    private val itemUseCases: ItemUseCases,
    private val vendorUseCases: VendorUseCases,
    private val tenantRepository: TenantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePurchaseUiState())
    val uiState: StateFlow<CreatePurchaseUiState> = _uiState.asStateFlow()

    private val purchaseId: String? = savedStateHandle["purchaseId"]
    val isEditMode = purchaseId != null
    private var existingPurchase: PurchaseEntity? = null

    init {
        loadInitialData()
        purchaseId?.let { loadPurchase(it) }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Load Vendors
            launch {
                when (val vendorsResult = vendorUseCases.getAll(Unit)) {
                    is ResultState.Success -> {
                        vendorsResult.data.collectLatest { list ->
                            _uiState.update { it.copy(vendors = list.filter { !it.isDeleted || it.vendorId == existingPurchase?.vendorId }) }
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

    private fun loadPurchase(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = purchaseUseCases.getById(id)) {
                is ResultState.Success -> {
                    val purchaseWithItems = result.data
                    existingPurchase = purchaseWithItems?.purchase
                    
                    purchaseWithItems?.let { data ->
                        val cartItems = data.items.mapNotNull { pItem ->
                            val item = uiState.value.items.find { it.itemId == pItem.itemId }
                            item?.let { PurchaseCartItem(it, pItem.quantity, pItem.price) }
                        }
                        
                        _uiState.update { state ->
                            state.copy(
                                selectedVendor = state.vendors.find { it.vendorId == data.purchase.vendorId },
                                cart = cartItems,
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

    fun onVendorSelected(vendor: VendorEntity?) {
        _uiState.update { it.copy(selectedVendor = vendor, isCashPurchase = vendor == null) }
    }

    fun onCashPurchaseToggle(isCash: Boolean) {
        _uiState.update { 
            it.copy(
                isCashPurchase = isCash,
                selectedVendor = if (isCash) null else it.selectedVendor
            )
        }
    }

    fun addItemToCart(item: ItemEntity, quantity: Double, costPrice: Double) {
        _uiState.update { state ->
            val existing = state.cart.find { it.item.itemId == item.itemId }
            val newCart = if (existing != null) {
                state.cart.map { 
                    if (it.item.itemId == item.itemId) it.copy(quantity = it.quantity + quantity) 
                    else it 
                }
            } else {
                state.cart + PurchaseCartItem(item, quantity, costPrice)
            }
            state.copy(cart = newCart)
        }
    }

    fun updateCartItemQuantity(cartItem: PurchaseCartItem, newQuantity: Double) {
        _uiState.update { state ->
            val newCart = state.cart.map { 
                if (it.item.itemId == cartItem.item.itemId) it.copy(quantity = newQuantity) 
                else it 
            }
            state.copy(cart = newCart)
        }
    }

    fun updateCartItemPrice(cartItem: PurchaseCartItem, newPrice: Double) {
        _uiState.update { state ->
            val newCart = state.cart.map { 
                if (it.item.itemId == cartItem.item.itemId) it.copy(costPrice = newPrice) 
                else it 
            }
            state.copy(cart = newCart)
        }
    }

    fun removeCartItem(cartItem: PurchaseCartItem) {
        _uiState.update { state ->
            state.copy(cart = state.cart - cartItem)
        }
    }

    fun setShowAddVendorDialog(show: Boolean) {
        _uiState.update { it.copy(
            showAddVendorDialog = show,
            vendorName = "",
            vendorPhone = "",
            vendorEmail = "",
            vendorAddress = "",
            billingAddress = "",
            state = "",
            openingBalance = "",
            gstNumber = "",
            taxType = TaxType.NONE,
            vendorError = null
        ) }
    }

    fun onVendorNameChange(value: String) {
        _uiState.update { it.copy(vendorName = value) }
    }

    fun onVendorPhoneChange(value: String) {
        _uiState.update { it.copy(vendorPhone = value) }
    }

    fun onVendorEmailChange(value: String) {
        _uiState.update { it.copy(vendorEmail = value) }
    }

    fun onVendorAddressChange(value: String) {
        _uiState.update { it.copy(vendorAddress = value) }
    }

    fun onVendorBillingAddressChange(value: String) {
        _uiState.update { it.copy(billingAddress = value) }
    }

    fun onVendorStateChange(value: String) {
        _uiState.update { it.copy(state = value) }
    }

    fun onVendorOpeningBalanceChange(value: String) {
        _uiState.update { it.copy(openingBalance = value) }
    }

    fun onVendorGstChange(value: String) {
        _uiState.update { it.copy(gstNumber = value) }
    }

    fun onVendorTaxTypeChange(value: TaxType) {
        _uiState.update { it.copy(taxType = value) }
    }

    fun saveNewVendor() {
        val state = uiState.value
        if (state.vendorName.isBlank()) {
            _uiState.update { it.copy(vendorError = "Name is required") }
            return
        }
        if (state.taxType != TaxType.NONE && state.gstNumber.isBlank()) {
            _uiState.update { it.copy(vendorError = "GST Number is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingVendor = true) }
            val newVendor = VendorEntity(
                vendorId = UUID.randomUUID().toString(),
                companyId = tenantRepository.getSelectedCompanyId(),
                locationId = tenantRepository.getSelectedLocationId(),
                name = state.vendorName,
                phone = state.vendorPhone.ifBlank { null },
                email = state.vendorEmail.ifBlank { null },
                address = state.vendorAddress.ifBlank { null },
                billingAddress = state.billingAddress.ifBlank { null },
                state = state.state.ifBlank { null },
                openingBalance = state.openingBalance.toDoubleOrNull() ?: 0.0,
                gstNumber = if (state.taxType != TaxType.NONE) state.gstNumber.ifBlank { null } else null,
                taxType = state.taxType.name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val result = vendorUseCases.add(AddVendorUseCase.AddVendorParams(newVendor))
            when (result) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(
                        isSavingVendor = false,
                        showAddVendorDialog = false,
                        selectedVendor = newVendor
                    ) }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isSavingVendor = false, vendorError = result.message) }
                }
                else -> {}
            }
        }
    }

    fun savePurchase(onSuccess: () -> Unit) {
        val state = uiState.value
        if (state.cart.isEmpty()) {
            _uiState.update { it.copy(error = "Cart is empty") }
            return
        }
        if (!state.isCashPurchase && state.selectedVendor == null) {
            _uiState.update { it.copy(error = "Please select a vendor or toggle Cash Purchase") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val currentPurchaseId = purchaseId ?: UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            
            val purchase = PurchaseEntity(
                purchaseId = currentPurchaseId,
                companyId = tenantRepository.getSelectedCompanyId(),
                locationId = tenantRepository.getSelectedLocationId(),
                vendorId = if (state.isCashPurchase) "CASH" else state.selectedVendor?.vendorId ?: "",
                invoiceNumber = existingPurchase?.invoiceNumber ?: "PUR-${now.toString().takeLast(6)}",
                totalAmount = state.finalAmount,
                createdAt = existingPurchase?.createdAt ?: now,
                updatedAt = now,
                isSynced = false,
                isDeleted = false
            )
            
            val purchaseItems = state.cart.map { cartItem ->
                PurchaseItemEntity(
                    purchaseItemId = UUID.randomUUID().toString(),
                    companyId = purchase.companyId,
                    locationId = purchase.locationId,
                    purchaseId = currentPurchaseId,
                    itemId = cartItem.item.itemId,
                    quantity = cartItem.quantity,
                    price = cartItem.costPrice,
                    total = cartItem.total,
                    createdAt = now,
                    updatedAt = now,
                    isSynced = false
                )
            }

            val result = if (isEditMode) {
                purchaseUseCases.update(UpdatePurchaseUseCase.UpdatePurchaseParams(purchase, purchaseItems))
            } else {
                purchaseUseCases.add(AddPurchaseUseCase.AddPurchaseParams(purchase, purchaseItems))
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
