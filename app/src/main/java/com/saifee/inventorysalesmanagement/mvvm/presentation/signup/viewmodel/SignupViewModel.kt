package com.saifee.inventorysalesmanagement.mvvm.presentation.signup.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val initialCompanyId: String = savedStateHandle.get<String>("companyId") ?: ""

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun register(email: String, pass: String, name: String, companyId: String, phone: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank() || name.isBlank() || companyId.isBlank() || phone.isBlank()) {
            _errorMessage.value = "Please fill in all required fields"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.register(email, pass, name, companyId, phone)
            _isLoading.value = false
            result.fold(
                onSuccess = { 
                    _successMessage.value = "Registration successful! Pending Admin approval."
                    onSuccess() 
                },
                onFailure = { _errorMessage.value = it.localizedMessage ?: "Registration failed" }
            )
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
