package com.saifee.inventorysalesmanagement.mvvm.data.repository

import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ActivityLogDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ActivityLogEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ActivityRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityLogDao: ActivityLogDao,
    private val tenantRepository: TenantRepository
) : ActivityRepository {

    private val tenantIdsFlow = combine(
        tenantRepository.selectedCompanyIdFlow,
        tenantRepository.selectedLocationIdFlow
    ) { cid, lid -> cid to lid }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLogs(): Flow<List<ActivityLogEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        activityLogDao.getLogs(cid ?: "", lid ?: "")
    }

    override suspend fun addLog(
        type: String,
        message: String,
        amount: Double?,
        transactionType: String?,
        entityId: String?,
        entityType: String?
    ) {
        val log = ActivityLogEntity(
            companyId = tenantRepository.getSelectedCompanyId(),
            locationId = tenantRepository.getSelectedLocationId(),
            type = type,
            message = message,
            amount = amount,
            transactionType = transactionType,
            entityId = entityId,
            entityType = entityType,
            createdAt = System.currentTimeMillis(),
            createdBy = tenantRepository.getSelectedUserId(),
            updatedBy = tenantRepository.getSelectedUserId()
        )
        activityLogDao.insertLog(log)
    }
}
