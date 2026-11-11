package com.saifee.inventorysalesmanagement.mvvm.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val prefs: SharedPref
) : ViewModel() {

    private val _lastSyncTime = MutableStateFlow(prefs.lastSyncTime)
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    fun triggerSync() {
        viewModelScope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            _syncMessage.value = null
            try {
                syncRepository.syncAll()
                syncRepository.triggerFullSync()
                prefs.lastSyncTime = System.currentTimeMillis()
                _lastSyncTime.value = prefs.lastSyncTime
                _syncMessage.value = "Synced successfully with server!"
            } catch (e: Exception) {
                e.printStackTrace()
                _syncMessage.value = "Sync failed: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isSyncing.value = false
            }
        }
    }
    
    fun clearMessage() {
        _syncMessage.value = null
    }
    
    fun updateLastSyncTime() {
        _lastSyncTime.value = prefs.lastSyncTime
    }
}
