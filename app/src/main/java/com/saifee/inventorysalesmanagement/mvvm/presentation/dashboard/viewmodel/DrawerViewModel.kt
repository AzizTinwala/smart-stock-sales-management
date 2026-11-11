package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.LocationEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DrawerUiState(
    val companies: List<CompanyEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val selectedCompanyId: String? = null,
    val selectedLocationId: String? = null,
    val currentUserProfile: UserProfile? = null,
    val canManageUsers: Boolean = false
)

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val repository: TenantRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrawerUiState())
    val uiState: StateFlow<DrawerUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.selectedCompanyIdFlow,
                repository.selectedLocationIdFlow,
                repository.getCompanies(),
                authRepository.currentUserProfileFlow
            ) { selectedCompanyId, selectedLocationId, companies, userProfile ->
                val filteredCompanies = if (userProfile?.role == UserRole.SUPER_USER) {
                    companies
                } else if (userProfile != null && userProfile.companyId.isNotEmpty()) {
                    companies.filter { it.companyId == userProfile.companyId }
                } else {
                    companies
                }

                val effectiveCompanyId = if (userProfile != null && userProfile.role != UserRole.SUPER_USER) {
                    userProfile.companyId
                } else {
                    selectedCompanyId ?: userProfile?.companyId
                }

                if (effectiveCompanyId != null && effectiveCompanyId != selectedCompanyId) {
                    repository.updateSelectedCompany(effectiveCompanyId)
                }

                val canManageUsers = authorizationManager.canManageUsers(userProfile, effectiveCompanyId ?: "")

                _uiState.update { 
                    it.copy(
                        selectedCompanyId = effectiveCompanyId,
                        selectedLocationId = selectedLocationId,
                        companies = filteredCompanies,
                        currentUserProfile = userProfile,
                        canManageUsers = canManageUsers
                    )
                }
                effectiveCompanyId
            }.collectLatest { companyId ->
                if (companyId != null && companyId.isNotEmpty()) {
                    repository.getLocations(companyId).collectLatest { locations ->
                        val currentLocId = _uiState.value.selectedLocationId
                        val effectiveLocationId = if (currentLocId.isNullOrEmpty() || locations.none { it.locationId == currentLocId }) {
                            locations.firstOrNull()?.locationId
                        } else {
                            currentLocId
                        }
                        if (effectiveLocationId != null && effectiveLocationId != currentLocId) {
                            repository.updateSelectedLocation(effectiveLocationId)
                        }
                        _uiState.update { 
                            it.copy(
                                locations = locations,
                                selectedLocationId = effectiveLocationId
                            ) 
                        }
                    }
                } else {
                    _uiState.update { it.copy(locations = emptyList(), selectedLocationId = null) }
                }
            }
        }
    }

    fun selectCompany(companyId: String?) {
        val user = uiState.value.currentUserProfile
        if (user != null && user.role != UserRole.SUPER_USER && companyId != user.companyId) {
            return // Non-Super User cannot switch to another company
        }
        repository.updateSelectedCompany(companyId)
        repository.updateSelectedLocation(null)
    }

    fun selectLocation(locationId: String?) {
        repository.updateSelectedLocation(locationId)
    }

    fun canAccessFeature(featureKey: String): Boolean {
        return authorizationManager.canAccessFeature(uiState.value.currentUserProfile, featureKey)
    }
}
