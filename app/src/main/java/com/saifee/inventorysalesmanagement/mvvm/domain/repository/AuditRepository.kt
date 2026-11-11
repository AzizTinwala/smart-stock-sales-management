package com.saifee.inventorysalesmanagement.mvvm.domain.repository

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.AuditLogEntity

interface AuditRepository {
    suspend fun logAction(
        action: String,
        targetId: String,
        companyId: String,
        details: String,
        beforeValue: String? = null,
        afterValue: String? = null
    )
    suspend fun getAuditLogs(companyId: String): List<AuditLogEntity>
}
