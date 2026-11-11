package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "location",
    indices = [Index("companyId")]
)
data class LocationEntity(
    @PrimaryKey
    val locationId: String = UUID.randomUUID().toString(),
    val companyId: String = "",
    val locationName: String = "",
    val address: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
