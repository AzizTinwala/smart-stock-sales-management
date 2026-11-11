package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sales_return",
    foreignKeys = [
        ForeignKey(
            entity = SalesEntity::class,
            parentColumns = ["salesId"],
            childColumns = ["salesId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("salesId"), Index("companyId"), Index("locationId")]
)
data class SalesReturnEntity(

    @PrimaryKey
    val salesReturnId: String = UUID.randomUUID().toString(),
    val companyId: String,
    val locationId: String,

    val salesId: String,

    val totalAmount: Double,
    val reason: String?,

    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false
)