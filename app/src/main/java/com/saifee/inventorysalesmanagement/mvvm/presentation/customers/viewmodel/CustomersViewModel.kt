package com.saifee.inventorysalesmanagement.mvvm.presentation.customers.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.core.enums.TaxType
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.AddCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.DeleteCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.UpdateCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.CustomerUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CustomerUiState(
    val customers: List<CustomerEntity> = emptyList(),
    val isLoading: Boolean = false,
    val showAddEditDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val editingCustomerId: String? = null,
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val billingAddress: String = "",
    val shippingAddress: String = "",
    val state: String = "",
    val openingBalance: String = "",
    val gstNumber: String = "",
    val taxType: TaxType = TaxType.NONE,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val useCases: CustomerUseCases,
    private val repository: TenantRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerUiState())
    val uiState: StateFlow<CustomerUiState> = _uiState.asStateFlow()

    private val customerId: String? = savedStateHandle["customerId"]

    init {
        viewModelScope.launch {
            combine(
                repository.selectedCompanyIdFlow,
                repository.selectedLocationIdFlow
            ) { cid, lid -> cid to lid }
                .collect { (cid, lid) ->
                    if (cid != null && lid != null) {
                        loadCustomers()
                    }
                }
        }
        customerId?.let { loadCustomer(it) }
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = useCases.getAll(Unit)) {
                is ResultState.Success -> {
                    result.data.collectLatest { list ->
                        _uiState.update { it.copy(isLoading = false, customers = list) }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun loadCustomer(id: String) {
        viewModelScope.launch {
            when (val result = useCases.getById(id)) {
                is ResultState.Success -> {
                    result.data?.let { customer ->
                        _uiState.update {
                            it.copy(
                                isEditMode = true,
                                editingCustomerId = customer.customerId,
                                name = customer.name,
                                phone = customer.phone ?: "",
                                email = customer.email ?: "",
                                address = customer.address ?: "",
                                billingAddress = customer.billingAddress ?: "",
                                shippingAddress = customer.shippingAddress ?: "",
                                state = customer.state ?: "",
                                openingBalance = customer.openingBalance.toString(),
                                gstNumber = customer.gstNumber ?: "",
                                taxType = try { TaxType.valueOf(customer.taxType) } catch (e: Exception) { TaxType.NONE },
                                showAddEditDialog = true
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onAddressChange(value: String) {
        _uiState.update { it.copy(address = value) }
    }

    fun onBillingAddressChange(value: String) {
        _uiState.update { it.copy(billingAddress = value) }
    }

    fun onShippingAddressChange(value: String) {
        _uiState.update { it.copy(shippingAddress = value) }
    }

    fun onStateChange(value: String) {
        _uiState.update { it.copy(state = value) }
    }

    fun onOpeningBalanceChange(value: String) {
        _uiState.update { it.copy(openingBalance = value) }
    }

    fun onGstNumberChange(value: String) {
        _uiState.update { it.copy(gstNumber = value) }
    }

    fun onTaxTypeChange(value: TaxType) {
        _uiState.update { it.copy(taxType = value) }
    }

    fun setShowAddEditDialog(show: Boolean, customer: CustomerEntity? = null) {
        if (show && customer != null) {
            _uiState.update {
                it.copy(
                    showAddEditDialog = true,
                    isEditMode = true,
                    editingCustomerId = customer.customerId,
                    name = customer.name,
                    phone = customer.phone ?: "",
                    email = customer.email ?: "",
                    address = customer.address ?: "",
                    billingAddress = customer.billingAddress ?: "",
                    shippingAddress = customer.shippingAddress ?: "",
                    state = customer.state ?: "",
                    openingBalance = customer.openingBalance.toString(),
                    gstNumber = customer.gstNumber ?: "",
                    taxType = try { TaxType.valueOf(customer.taxType) } catch (e: Exception) { TaxType.NONE },
                    errorMessage = null
                )
            }
        } else if (show) {
            _uiState.update {
                it.copy(
                    showAddEditDialog = true,
                    isEditMode = false,
                    editingCustomerId = null,
                    name = "",
                    phone = "",
                    email = "",
                    address = "",
                    billingAddress = "",
                    shippingAddress = "",
                    state = "",
                    openingBalance = "",
                    gstNumber = "",
                    taxType = TaxType.NONE,
                    errorMessage = null
                )
            }
        } else {
            _uiState.update { it.copy(showAddEditDialog = false) }
        }
    }

    fun saveCustomer(onSuccess: () -> Unit) {
        val state = uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name is required") }
            return
        }
        if (state.taxType != TaxType.NONE && state.gstNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "GST Number is required for selected Tax Type") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val customer = CustomerEntity(
                customerId = state.editingCustomerId ?: UUID.randomUUID().toString(),
                companyId = repository.getSelectedCompanyId(),
                locationId = repository.getSelectedLocationId(),
                name = state.name,
                phone = state.phone.ifBlank { null },
                email = state.email.ifBlank { null },
                address = state.address.ifBlank { null },
                billingAddress = state.billingAddress.ifBlank { null },
                shippingAddress = state.shippingAddress.ifBlank { null },
                state = state.state.ifBlank { null },
                openingBalance = state.openingBalance.toDoubleOrNull() ?: 0.0,
                gstNumber = if (state.taxType != TaxType.NONE) state.gstNumber.ifBlank { null } else null,
                taxType = state.taxType.name,
                createdAt = if (state.isEditMode) 0L else System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val result = if (state.isEditMode) {
                useCases.update(UpdateCustomerUseCase.UpdateCustomerParams(customer))
            } else {
                useCases.add(AddCustomerUseCase.AddCustomerParams(customer))
            }

            when (result) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(isSaving = false, showAddEditDialog = false) }
                    onSuccess()
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            useCases.delete(DeleteCustomerUseCase.DeleteCustomerParams(customer))
        }
    }
}
