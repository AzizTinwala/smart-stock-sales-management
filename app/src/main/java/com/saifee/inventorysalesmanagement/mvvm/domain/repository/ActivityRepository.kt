package com.saifee.inventorysalesmanagement.mvvm.domain.repository

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getLogs(): Flow<List<ActivityLogEntity>>
    suspend fun addLog(
        type: String,
        message: String,
        amount: Double? = null,
        transactionType: String? = null,
        entityId: String? = null,
        entityType: String? = null
    )
}
