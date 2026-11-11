package com.saifee.inventorysalesmanagement.mvvm.core

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.saifee.inventorysalesmanagement.mvvm.core.common_utils.LocalFileHelper
import com.saifee.inventorysalesmanagement.mvvm.data.worker.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var localFileHelper: LocalFileHelper

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        localFileHelper.clearAllTemps()
        registerWorkManager()
    }

    private fun registerWorkManager() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "sync_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}