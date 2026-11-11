package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sales",
    indices = [
        Index("companyId"),
        Index("locationId"),
        Index(value = ["companyId", "locationId", "isDeleted"]),
        Index(value = ["companyId", "locationId", "isSynced"])
    ]
)
data class SalesEntity(

    @PrimaryKey
    val salesId: String = UUID.randomUUID().toString(),

    val companyId: String = "",
    val locationId: String = "",

    val invoiceNumber: String = "",
    val customerId: String? = null,
    
    val totalAmount: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val finalAmount: Double = 0.0,
    val status: String = "PAID", // PAID / PARTIAL / UNPAID

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
