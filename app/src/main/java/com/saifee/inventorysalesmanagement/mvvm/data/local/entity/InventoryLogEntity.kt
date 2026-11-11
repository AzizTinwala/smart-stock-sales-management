package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_log",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("itemId"), Index("companyId"), Index("locationId")]
)
data class InventoryLogEntity(

    @PrimaryKey(autoGenerate = true)
    val inventoryId: Long = 0,
    val companyId: String,
    val locationId: String,

    val itemId: Long,

    val changeType: String, // IN / OUT / ADJUST
    val quantity: Double,
    val reason: String?,
    val referenceId: String?, // salesId / purchaseId

    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false
)