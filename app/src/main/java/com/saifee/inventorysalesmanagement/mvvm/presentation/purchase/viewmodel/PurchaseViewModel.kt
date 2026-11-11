package com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.DeletePurchaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.PurchaseUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PurchaseUiState(
    val purchases: List<PurchaseEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val useCases: PurchaseUseCases,
    private val repository: TenantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseUiState())
    val uiState: StateFlow<PurchaseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.selectedCompanyIdFlow,
                repository.selectedLocationIdFlow
            ) { cid, lid -> cid to lid }
                .collect { (cid, lid) ->
                    if (cid != null && lid != null) {
                        loadPurchases()
                    }
                }
        }
    }

    private fun loadPurchases() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = useCases.getAll(Unit)) {
                is ResultState.Success -> {
                    result.data.collectLatest { list ->
                        _uiState.update { it.copy(isLoading = false, purchases = list) }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun deletePurchase(purchase: PurchaseEntity) {
        viewModelScope.launch {
            useCases.delete(DeletePurchaseUseCase.DeletePurchaseParams(purchase.purchaseId))
        }
    }
}
