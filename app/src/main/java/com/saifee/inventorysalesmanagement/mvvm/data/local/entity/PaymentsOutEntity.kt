package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "payments_out",
    foreignKeys = [
        ForeignKey(
            entity = VendorEntity::class,
            parentColumns = ["vendorId"],
            childColumns = ["vendorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vendorId"), Index("companyId"), Index("locationId")]
)
data class PaymentsOutEntity(

    @PrimaryKey
    val paymentOutId: String = UUID.randomUUID().toString(),
    val companyId: String = "",
    val locationId: String = "",
    val vendorId: String = "",
    val amount: Double = 0.0,
    val paymentMode: String = "", // CASH, UPI, BANK
    val reference: String? = null,
    val purchaseId: String? = null, // in PaymentsOut
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false
)
