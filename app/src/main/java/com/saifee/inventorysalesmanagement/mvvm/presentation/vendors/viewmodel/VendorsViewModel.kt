package com.saifee.inventorysalesmanagement.mvvm.presentation.vendors.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.core.enums.TaxType
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.AddVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.DeleteVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.UpdateVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.VendorUseCases
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

data class VendorUiState(
    val vendors: List<VendorEntity> = emptyList(),
    val isLoading: Boolean = false,
    val showAddEditDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val editingVendorId: String? = null,
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val billingAddress: String = "",
    val state: String = "",
    val openingBalance: String = "",
    val gstNumber: String = "",
    val taxType: TaxType = TaxType.NONE,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class VendorsViewModel @Inject constructor(
    private val useCases: VendorUseCases,
    private val repository: TenantRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorUiState())
    val uiState: StateFlow<VendorUiState> = _uiState.asStateFlow()

    private val vendorId: String? = savedStateHandle["vendorId"]

    init {
        viewModelScope.launch {
            combine(
                repository.selectedCompanyIdFlow,
                repository.selectedLocationIdFlow
            ) { cid, lid -> cid to lid }
                .collect { (cid, lid) ->
                    if (cid != null && lid != null) {
                        loadVendors()
                    }
                }
        }
        vendorId?.let { loadVendor(it) }
    }

    private fun loadVendors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = useCases.getAll(Unit)) {
                is ResultState.Success -> {
                    result.data.collectLatest { list ->
                        _uiState.update { it.copy(isLoading = false, vendors = list) }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun loadVendor(id: String) {
        viewModelScope.launch {
            when (val result = useCases.getById(id)) {
                is ResultState.Success -> {
                    result.data?.let { vendor ->
                        _uiState.update {
                            it.copy(
                                isEditMode = true,
                                editingVendorId = vendor.vendorId,
                                name = vendor.name,
                                phone = vendor.phone ?: "",
                                email = vendor.email ?: "",
                                address = vendor.address ?: "",
                                billingAddress = vendor.billingAddress ?: "",
                                state = vendor.state ?: "",
                                openingBalance = vendor.openingBalance.toString(),
                                gstNumber = vendor.gstNumber ?: "",
                                taxType = try { TaxType.valueOf(vendor.taxType) } catch (e: Exception) { TaxType.NONE },
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

    fun setShowAddEditDialog(show: Boolean, vendor: VendorEntity? = null) {
        if (show && vendor != null) {
            _uiState.update {
                it.copy(
                    showAddEditDialog = true,
                    isEditMode = true,
                    editingVendorId = vendor.vendorId,
                    name = vendor.name,
                    phone = vendor.phone ?: "",
                    email = vendor.email ?: "",
                    address = vendor.address ?: "",
                    billingAddress = vendor.billingAddress ?: "",
                    state = vendor.state ?: "",
                    openingBalance = vendor.openingBalance.toString(),
                    gstNumber = vendor.gstNumber ?: "",
                    taxType = try { TaxType.valueOf(vendor.taxType) } catch (e: Exception) { TaxType.NONE },
                    errorMessage = null
                )
            }
        } else if (show) {
            _uiState.update {
                it.copy(
                    showAddEditDialog = true,
                    isEditMode = false,
                    editingVendorId = null,
                    name = "",
                    phone = "",
                    email = "",
                    address = "",
                    billingAddress = "",
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

    fun saveVendor(onSuccess: () -> Unit) {
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
            val vendor = VendorEntity(
                vendorId = state.editingVendorId ?: UUID.randomUUID().toString(),
                companyId = repository.getSelectedCompanyId(),
                locationId = repository.getSelectedLocationId(),
                name = state.name,
                phone = state.phone.ifBlank { null },
                email = state.email.ifBlank { null },
                address = state.address.ifBlank { null },
                billingAddress = state.billingAddress.ifBlank { null },
                state = state.state.ifBlank { null },
                openingBalance = state.openingBalance.toDoubleOrNull() ?: 0.0,
                gstNumber = if (state.taxType != TaxType.NONE) state.gstNumber.ifBlank { null } else null,
                taxType = state.taxType.name,
                createdAt = if (state.isEditMode) 0L else System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val result = if (state.isEditMode) {
                useCases.update(UpdateVendorUseCase.UpdateVendorParams(vendor))
            } else {
                useCases.add(AddVendorUseCase.AddVendorParams(vendor))
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

    fun deleteVendor(vendor: VendorEntity) {
        viewModelScope.launch {
            useCases.delete(DeleteVendorUseCase.DeleteVendorParams(vendor))
        }
    }
}
