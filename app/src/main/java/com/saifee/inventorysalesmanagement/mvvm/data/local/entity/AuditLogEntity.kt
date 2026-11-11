package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey
    val logId: String = "",
    val performedByUserId: String = "",
    val targetId: String = "",
    val action: String = "",
    val companyId: String = "",
    val details: String = "",
    val beforeValue: String? = null,
    val afterValue: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
