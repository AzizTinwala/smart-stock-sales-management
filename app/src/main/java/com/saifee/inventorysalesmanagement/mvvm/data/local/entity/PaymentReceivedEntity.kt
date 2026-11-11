package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "payment_received",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["customerId"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId"), Index("companyId"), Index("locationId")]
)
data class PaymentReceivedEntity(

    @PrimaryKey
    val paymentReceivedId: String = UUID.randomUUID().toString(),
    val companyId: String = "",
    val locationId: String = "",
    val customerId: String = "",
    val amount: Double = 0.0,
    val paymentMode: String = "",
    val reference: String? = null,
    val salesId: String? = null, // in PaymentReceived
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false
)
