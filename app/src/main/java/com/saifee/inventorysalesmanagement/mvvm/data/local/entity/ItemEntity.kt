package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item",
    foreignKeys = [
        ForeignKey(
            entity = ItemGroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("groupId"),
        Index("companyId"),
        Index("locationId"),
        Index(value = ["companyId", "locationId", "isDeleted"]),
        Index(value = ["companyId", "locationId", "isSynced"])
    ]
)
data class ItemEntity(

    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0,

    val companyId: String = "",
    val locationId: String = "",

    val itemName: String = "",
    val groupId: Long = 0L,

    val sku: String? = null,
    val barcode: String? = null,

    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val stockQty: Double = 0.0,
    val unit: String? = null,
    val gstPercentage: Double = 0.0,
    val hsnNumber: String? = null,
    val lowStockLevel: Double = 5.0,
    val criticalStockLevel: Double = 2.0,

    val imagePath: String? = null,
    val imageUrl: String? = null,

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
