package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "vendor",
    indices = [
        Index("companyId"),
        Index("locationId"),
        Index(value = ["companyId", "locationId", "isDeleted"]),
        Index(value = ["companyId", "locationId", "isSynced"])
    ]
)
data class VendorEntity(

    @PrimaryKey
    val vendorId: String = UUID.randomUUID().toString(),

    val companyId: String = "",
    val locationId: String = "",

    val name: String = "",
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val billingAddress: String? = null,
    val state: String? = null,
    val openingBalance: Double = 0.0,
    val gstNumber: String? = null,
    val taxType: String = "NONE", // NONE, INTRA_STATE, INTER_STATE

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
