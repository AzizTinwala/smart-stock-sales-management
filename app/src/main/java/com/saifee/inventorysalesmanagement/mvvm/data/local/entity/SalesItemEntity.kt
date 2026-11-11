package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sales_item",
    foreignKeys = [
        ForeignKey(
            entity = SalesEntity::class,
            parentColumns = ["salesId"],
            childColumns = ["salesId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("salesId"), Index("itemId"), Index("companyId"), Index("locationId")]
)
data class SalesItemEntity(

    @PrimaryKey
    val salesItemId: String = UUID.randomUUID().toString(),

    val companyId: String,
    val locationId: String,

    val salesId: String,
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