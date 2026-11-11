package com.saifee.inventorysalesmanagement.mvvm.presentation.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyConfigViewModel @Inject constructor(
    private val tenantRepository: TenantRepository
) : ViewModel() {

    private val _companyId = MutableStateFlow("")
    val companyId: StateFlow<String> = _companyId.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onCompanyIdChanged(id: String) {
        _companyId.value = id
        _errorMessage.value = null
    }

    fun validateAndProceed(onSuccess: (String) -> Unit) {
        val cid = _companyId.value.trim()
        if (cid.isBlank()) {
            _errorMessage.value = "Please enter a valid Company ID or Code."
            return
        }
        viewModelScope.launch {
            onSuccess(cid)
        }
    }
}
