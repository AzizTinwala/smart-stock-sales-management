package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_logs",
    indices = [Index("companyId"), Index("locationId")]
)
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val companyId: String,
    val locationId: String,
    val type: String,          // ITEM_ADDED, SALE_CREATED, CUSTOMER_UPDATED
    val message: String,       // "Added item: Sugar"
    val amount: Double? = null,
    val transactionType: String? = null, // INCOME / EXPENSE
    val entityId: String? = null,       // Changed to String to match UUIDs
    val entityType: String? = null,   // ITEM, ORDER, CUSTOMER
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val updatedBy: String = ""
)
