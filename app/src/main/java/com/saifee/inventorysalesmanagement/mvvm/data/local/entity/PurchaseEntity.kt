package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "purchase",
    foreignKeys = [
        ForeignKey(
            entity = VendorEntity::class,
            parentColumns = ["vendorId"],
            childColumns = ["vendorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("vendorId"),
        Index("companyId"),
        Index("locationId"),
        Index(value = ["companyId", "locationId", "isDeleted"]),
        Index(value = ["companyId", "locationId", "isSynced"])
    ]
)
data class PurchaseEntity(

    @PrimaryKey
    val purchaseId: String = UUID.randomUUID().toString(),

    val companyId: String = "",
    val locationId: String = "",

    val vendorId: String = "",
    val invoiceNumber: String = "",

    val totalAmount: Double = 0.0,

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
