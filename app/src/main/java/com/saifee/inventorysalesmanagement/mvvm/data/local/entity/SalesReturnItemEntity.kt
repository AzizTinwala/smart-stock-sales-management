package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sales_return_item",
    foreignKeys = [
        ForeignKey(
            entity = SalesReturnEntity::class,
            parentColumns = ["salesReturnId"],
            childColumns = ["salesReturnId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("salesReturnId"), Index("itemId"), Index("companyId"), Index("locationId")]
)
data class SalesReturnItemEntity(

    @PrimaryKey
    val salesReturnItemId: String = UUID.randomUUID().toString(),
    val companyId: String,
    val locationId: String,

    val salesReturnId: String,
    val itemId: Long,

    val quantity: Double,
    val price: Double,

    val itemCondition: String = "SELLABLE", // SELLABLE or DAMAGED
    val restockAction: String = "RESTOCK",  // RESTOCK or QUARANTINE

    val updatedAt: Long,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false
)