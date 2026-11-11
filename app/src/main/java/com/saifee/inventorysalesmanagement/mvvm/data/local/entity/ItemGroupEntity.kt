package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_group",
    indices = [Index("companyId"), Index("locationId")]
)
data class ItemGroupEntity(

    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,

    val companyId: String = "",
    val locationId: String = "",

    val groupName: String = "",
    val description: String? = null,

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
