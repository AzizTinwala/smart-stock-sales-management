package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.CompanyDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.LocationDao
import com.saifee.inventorysalesmanagement.mvvm.domain.model.DashboardStats
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.dashboard.GetDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val stats: DashboardStats = DashboardStats(),
    val userName: String = "User",
    val companyName: String = "My Business",
    val warehouseName: String = "Main Branch"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val repository: TenantRepository,
    private val authRepository: AuthRepository,
    private val companyDao: CompanyDao,
    private val locationDao: LocationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var statsJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            val userFlow = authRepository.currentUserProfileFlow
            combine(
                repository.selectedCompanyIdFlow,
                repository.selectedLocationIdFlow,
                userFlow
            ) { companyId, locationId, userProfile ->
                val userName = userProfile?.name?.ifEmpty { userProfile.email.substringBefore("@") } ?: "User"
                val company = companyId?.let { companyDao.getById(it) }
                val location = locationId?.let { locationDao.getById(it) }
                Triple(
                    userName,
                    company?.companyName ?: "My Business",
                    location?.locationName ?: "Main Branch"
                )
            }.collectLatest { (userName, companyName, warehouseName) ->
                _uiState.update { it.copy(userName = userName, companyName = companyName, warehouseName = warehouseName) }
                loadStats()
            }
        }
    }

    private fun loadStats() {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            when (val result = getDashboardStatsUseCase(Unit)) {
                is ResultState.Success -> {
                    result.data.collect { newStats ->
                        _uiState.update { it.copy(stats = newStats) }
                    }
                }
                is ResultState.Error -> {
                    // Handle error
                }
                is ResultState.Loading -> {
                    // Handle loading if needed
                }
            }
        }
    }
}
