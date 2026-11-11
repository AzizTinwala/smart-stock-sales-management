package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchase_batch",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("itemId"), Index("companyId"), Index("locationId")]
)
data class PurchaseBatchEntity(
    @PrimaryKey(autoGenerate = true)
    val batchId: Long = 0L,
    val companyId: String = "",
    val locationId: String = "",
    val itemId: Long = 0L,
    val purchaseId: Long = 0L,
    val quantityPurchased: Double = 0.0,
    val quantityRemaining: Double = 0.0,
    val unitCost: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
