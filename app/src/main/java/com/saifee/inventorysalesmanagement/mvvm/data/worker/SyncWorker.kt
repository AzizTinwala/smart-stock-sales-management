package com.saifee.inventorysalesmanagement.mvvm.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val syncRepository: SyncRepository,
    private val prefs: SharedPref
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            syncRepository.syncAll()
            prefs.lastSyncTime = System.currentTimeMillis()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
