package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity

@Dao
interface SalesItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SalesItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SalesItemEntity>): List<Long>

    @Query("SELECT * FROM sales_item WHERE salesId = :salesId AND companyId = :companyId AND locationId = :locationId")
    suspend fun getBySalesId(salesId: String, companyId: String, locationId: String): List<SalesItemEntity>

    @Query("SELECT * FROM sales_item WHERE companyId = :companyId")
    fun getAllByCompany(companyId: String): kotlinx.coroutines.flow.Flow<List<SalesItemEntity>>

    @Query("SELECT * FROM sales_item WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<SalesItemEntity>

    @Query("DELETE FROM sales_item WHERE salesId = :salesId AND companyId = :companyId AND locationId = :locationId")
    suspend fun deleteBySalesId(salesId: String, companyId: String, locationId: String)
}