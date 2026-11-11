package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.InventoryLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: InventoryLogEntity): Long

    @Query("SELECT * FROM inventory_log WHERE itemId = :itemId AND companyId = :companyId AND locationId = :locationId")
    fun getByItem(itemId: Long, companyId: String, locationId: String): Flow<List<InventoryLogEntity>>

    @Query("SELECT * FROM inventory_log WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<InventoryLogEntity>

    @Query("SELECT * FROM inventory_log WHERE inventoryId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: Long, companyId: String, locationId: String): InventoryLogEntity?
}