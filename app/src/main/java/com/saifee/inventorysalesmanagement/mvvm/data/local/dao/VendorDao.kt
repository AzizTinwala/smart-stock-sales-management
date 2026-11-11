package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vendor: VendorEntity)

    @Query("SELECT * FROM vendor WHERE companyId = :companyId AND locationId = :locationId")
    fun getAll(companyId: String, locationId: String): Flow<List<VendorEntity>>

    @Query("SELECT * FROM vendor WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<VendorEntity>

    @Query("SELECT * FROM vendor WHERE vendorId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: String, companyId: String, locationId: String): VendorEntity?
}