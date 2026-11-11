package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "purchase_item",
    foreignKeys = [
        ForeignKey(
            entity = PurchaseEntity::class,
            parentColumns = ["purchaseId"],
            childColumns = ["purchaseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("purchaseId"), Index("itemId"), Index("companyId"), Index("locationId")]
)
data class PurchaseItemEntity(

    @PrimaryKey
    val purchaseItemId: String = UUID.randomUUID().toString(),
    val companyId: String,
    val locationId: String,

    val purchaseId: String,
    val itemId: Long,

    val quantity: Double,
    val price: Double,
    val total: Double,

    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false
)