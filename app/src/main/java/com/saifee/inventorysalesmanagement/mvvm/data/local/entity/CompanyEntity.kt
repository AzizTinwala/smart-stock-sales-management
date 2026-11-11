package com.saifee.inventorysalesmanagement.mvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "company")
data class CompanyEntity(
    @PrimaryKey
    val companyId: String = UUID.randomUUID().toString(),
    val companyName: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val gstNumber: String = "",
    val website: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val updatedBy: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
