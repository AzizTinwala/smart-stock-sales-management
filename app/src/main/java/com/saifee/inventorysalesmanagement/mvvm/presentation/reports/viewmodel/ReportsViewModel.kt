package com.saifee.inventorysalesmanagement.mvvm.presentation.reports.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.domain.model.ReportStats
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.reports.GetReportStatsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReportStatsUseCase: GetReportStatsUseCase,
    private val repository: TenantRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(ReportStats())
    val stats: StateFlow<ReportStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.selectedCompanyIdFlow,
                repository.selectedLocationIdFlow
            ) { cid, lid -> cid to lid }
                .collect { (cid, lid) ->
                    if (cid != null && lid != null) {
                        loadReports()
                    }
                }
        }
    }

    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = getReportStatsUseCase(Unit)) {
                is ResultState.Success -> {
                    result.data.collect { newStats ->
                        _stats.value = newStats
                        _isLoading.value = false
                    }
                }
                is ResultState.Error -> {
                    _isLoading.value = false
                }
                is ResultState.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
}
