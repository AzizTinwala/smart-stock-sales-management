package com.saifee.inventorysalesmanagement.mvvm.presentation.sales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.DeleteSaleUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.SalesUseCases
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

data class SalesUiState(
    val sales: List<SalesEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val useCases: SalesUseCases,
    private val repository: TenantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            combine(
                repository.selectedCompanyIdFlow,
                repository.selectedLocationIdFlow
            ) { cid, lid -> cid to lid }
                .collectLatest { (cid, lid) ->
                    if (cid != null && lid != null) {
                        loadSales()
                    }
                }
        }
    }

    private fun loadSales() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = useCases.getAll(Unit)) {
                is ResultState.Success -> {
                    result.data.collectLatest { list ->
                        _uiState.update { it.copy(isLoading = false, sales = list) }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun deleteSale(sale: SalesEntity) {
        viewModelScope.launch {
            useCases.delete(DeleteSaleUseCase.DeleteSaleParams(sale.salesId))
        }
    }
}
