package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnItemEntity

@Dao
interface SalesReturnItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SalesReturnItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SalesReturnItemEntity>): List<Long>

    @Query("SELECT * FROM sales_return_item WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<SalesReturnItemEntity>
}