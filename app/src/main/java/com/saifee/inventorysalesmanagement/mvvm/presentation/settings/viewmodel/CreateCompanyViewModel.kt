package com.saifee.inventorysalesmanagement.mvvm.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.LocationEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuditRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateCompanyViewModel @Inject constructor(
    private val tenantRepository: TenantRepository,
    private val authRepository: AuthRepository,
    private val auditRepository: AuditRepository,
    private val authorizationManager: AuthorizationManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun createCompany(
        name: String,
        email: String,
        phone: String,
        address: String,
        gstNumber: String,
        website: String,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || address.isBlank()) {
            _errorMessage.value = "Company Name, Email, Phone Number, and Address are mandatory."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = authRepository.getCurrentUserProfile()
            if (!authorizationManager.canCreateCompany(currentUser)) {
                _isLoading.value = false
                _errorMessage.value = "Unauthorized: Only Super User can create companies."
                return@launch
            }

            try {
                val companyId = UUID.randomUUID().toString()
                val company = CompanyEntity(
                    companyId = companyId,
                    companyName = name.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    address = address.trim(),
                    gstNumber = gstNumber.trim(),
                    website = website.trim(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    createdBy = currentUser?.userId ?: "super_user",
                    updatedBy = currentUser?.userId ?: "super_user"
                )

                tenantRepository.addCompany(company)

                val location = LocationEntity(
                    locationId = UUID.randomUUID().toString(),
                    companyId = companyId,
                    locationName = "Main Branch",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    createdBy = currentUser?.userId ?: "super_user",
                    updatedBy = currentUser?.userId ?: "super_user"
                )
                tenantRepository.addLocation(location)

                auditRepository.logAction(
                    action = "COMPANY_CREATED",
                    targetId = companyId,
                    companyId = companyId,
                    details = "Super User created company: ${company.companyName} (Email: $email, Phone: $phone)"
                )

                _isLoading.value = false
                _successMessage.value = "Company created successfully!"
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to create company."
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
