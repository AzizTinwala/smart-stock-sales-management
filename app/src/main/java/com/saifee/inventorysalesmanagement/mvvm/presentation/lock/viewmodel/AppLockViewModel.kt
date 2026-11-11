package com.saifee.inventorysalesmanagement.mvvm.presentation.lock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLockViewModel @Inject constructor(
    private val prefs: SharedPref,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    val lockType: String
        get() = prefs.appLockType

    val businessName: String
        get() = prefs.businessName

    fun unlockApp() {
        _isUnlocked.value = true
    }

    fun verifyPin(enteredPin: String, onSuccess: () -> Unit) {
        val savedPin = prefs.appPinCode
        if (savedPin.isEmpty() || savedPin == enteredPin) {
            _pinError.value = null
            _isUnlocked.value = true
            onSuccess()
        } else {
            _pinError.value = "Incorrect PIN. Try again."
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _isUnlocked.value = true
            onLogout()
        }
    }

    fun clearError() {
        _pinError.value = null
    }
}
