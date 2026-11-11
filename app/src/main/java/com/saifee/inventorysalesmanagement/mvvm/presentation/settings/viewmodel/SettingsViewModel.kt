package com.saifee.inventorysalesmanagement.mvvm.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.core.enums.AppTheme
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.LocationEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPref: SharedPref,
    private val repository: TenantRepository,
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager
) : ViewModel() {

    private val _themeState = MutableStateFlow(sharedPref.appTheme)
    val themeState: StateFlow<AppTheme> = _themeState

    private val _roundOffState = MutableStateFlow(sharedPref.defaultRoundOff)
    val roundOffState: StateFlow<Boolean> = _roundOffState

    private val _isAppLockEnabledState = MutableStateFlow(sharedPref.isAppLockEnabled)
    val isAppLockEnabledState: StateFlow<Boolean> = _isAppLockEnabledState

    private val _appLockTypeState = MutableStateFlow(sharedPref.appLockType)
    val appLockTypeState: StateFlow<String> = _appLockTypeState

    fun setAppLockEnabled(enabled: Boolean) {
        _isAppLockEnabledState.value = enabled
        sharedPref.isAppLockEnabled = enabled
    }

    fun setAppLockType(type: String) {
        _appLockTypeState.value = type
        sharedPref.appLockType = type
    }

    fun setAppPinCode(pin: String) {
        sharedPref.appPinCode = pin
    }

    val companies: StateFlow<List<CompanyEntity>> = repository.getCompanies()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedCompanyId: StateFlow<String?> = repository.selectedCompanyIdFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, sharedPref.selectedCompanyId)

    val selectedLocationId: StateFlow<String?> = repository.selectedLocationIdFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, sharedPref.selectedLocationId)

    val locations: StateFlow<List<LocationEntity>> = selectedCompanyId.flatMapLatest { id ->
        if (id != null) repository.getLocations(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setTheme(theme: AppTheme) {
        _themeState.value = theme
        sharedPref.appTheme = theme
    }

    fun setRoundOff(enabled: Boolean) {
        _roundOffState.value = enabled
        sharedPref.defaultRoundOff = enabled
    }

    fun addCompany(name: String) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUserProfile()
            if (!authorizationManager.canCreateCompany(user)) return@launch

            val company = CompanyEntity(
                companyId = UUID.randomUUID().toString(),
                companyName = name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.addCompany(company)
            
            // Create a default location for the new company
            val location = LocationEntity(
                locationId = UUID.randomUUID().toString(),
                companyId = company.companyId,
                locationName = "Main Branch",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.addLocation(location)

            if (selectedCompanyId.value == null) {
                selectCompany(company.companyId)
                selectLocation(location.locationId)
            }
        }
    }

    fun addLocation(companyId: String, name: String) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUserProfile()
            if (!authorizationManager.canManageSettings(user, companyId)) return@launch

            val location = LocationEntity(
                locationId = UUID.randomUUID().toString(),
                companyId = companyId,
                locationName = name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.addLocation(location)
            if (selectedLocationId.value == null && selectedCompanyId.value == companyId) {
                selectLocation(location.locationId)
            }
        }
    }

    fun selectCompany(companyId: String) {
        repository.updateSelectedCompany(companyId)
        // Reset location when company changes
        repository.updateSelectedLocation(null)
    }

    fun selectLocation(locationId: String) {
        repository.updateSelectedLocation(locationId)
    }

    fun deleteCompany(company: CompanyEntity) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUserProfile()
            if (!authorizationManager.canDeleteCompany(user)) return@launch

            repository.deleteCompany(company)
            if (selectedCompanyId.value == company.companyId) {
                repository.updateSelectedCompany(null)
                repository.updateSelectedLocation(null)
            }
        }
    }

    fun deleteLocation(location: LocationEntity) {
        viewModelScope.launch {
            repository.deleteLocation(location)
            if (selectedLocationId.value == location.locationId) {
                repository.updateSelectedLocation(null)
            }
        }
    }

    fun refreshAllData() {
        syncRepository.triggerFullSync()
    }

    private val _businessName = MutableStateFlow(sharedPref.businessName)
    val businessName: StateFlow<String> = _businessName

    private val _tagline = MutableStateFlow(sharedPref.tagline)
    val tagline: StateFlow<String> = _tagline

    private val _gstNumber = MutableStateFlow(sharedPref.gstNumber)
    val gstNumber: StateFlow<String> = _gstNumber

    private val _phone = MutableStateFlow(sharedPref.phone)
    val phone: StateFlow<String> = _phone

    private val _email = MutableStateFlow(sharedPref.email)
    val email: StateFlow<String> = _email

    private val _address = MutableStateFlow(sharedPref.address)
    val address: StateFlow<String> = _address

    private val _currencySymbol = MutableStateFlow(sharedPref.currencySymbol)
    val currencySymbol: StateFlow<String> = _currencySymbol

    private val _defaultTaxRate = MutableStateFlow(sharedPref.defaultTaxRate.toString())
    val defaultTaxRate: StateFlow<String> = _defaultTaxRate

    private val _invoicePrefix = MutableStateFlow(sharedPref.invoicePrefix)
    val invoicePrefix: StateFlow<String> = _invoicePrefix

    private val _isDeviceFrameMode = MutableStateFlow(sharedPref.isDeviceFrameMode)
    val isDeviceFrameMode: StateFlow<Boolean> = _isDeviceFrameMode

    private val _savedSuccess = MutableStateFlow(false)
    val savedSuccess: StateFlow<Boolean> = _savedSuccess

    fun updateBusinessSettings(
        bName: String,
        bTagline: String,
        bGst: String,
        bPhone: String,
        bEmail: String,
        bAddress: String,
        bCurrency: String,
        bTaxRate: String,
        bPrefix: String
    ) {
        sharedPref.businessName = bName
        sharedPref.tagline = bTagline
        sharedPref.gstNumber = bGst
        sharedPref.phone = bPhone
        sharedPref.email = bEmail
        sharedPref.address = bAddress
        sharedPref.currencySymbol = bCurrency
        val taxFloat = bTaxRate.toFloatOrNull() ?: 18.0f
        sharedPref.defaultTaxRate = taxFloat
        sharedPref.invoicePrefix = bPrefix

        _businessName.value = bName
        _tagline.value = bTagline
        _gstNumber.value = bGst
        _phone.value = bPhone
        _email.value = bEmail
        _address.value = bAddress
        _currencySymbol.value = bCurrency
        _defaultTaxRate.value = taxFloat.toString()
        _invoicePrefix.value = bPrefix

        selectedCompanyId.value?.let { cid ->
            viewModelScope.launch {
                val company = repository.getCompanyById(cid)
                if (company != null) {
                    repository.addCompany(
                        company.copy(
                            companyName = bName.ifBlank { company.companyName },
                            address = bAddress,
                            phone = bPhone,
                            email = bEmail,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            _savedSuccess.value = true
            kotlinx.coroutines.delay(2500)
            _savedSuccess.value = false
        }
    }

    fun toggleDeviceFrameMode() {
        val newMode = !_isDeviceFrameMode.value
        _isDeviceFrameMode.value = newMode
        sharedPref.isDeviceFrameMode = newMode
    }

    fun resetDemoData() {
        syncRepository.triggerFullSync()
    }

    fun changeOwnPassword(currentPass: String, newPass: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.changeOwnPassword(currentPass, newPass)
            onResult(result)
        }
    }
}
