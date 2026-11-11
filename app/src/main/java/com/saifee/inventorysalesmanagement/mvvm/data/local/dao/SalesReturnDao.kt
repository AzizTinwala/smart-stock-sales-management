package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnEntity

@Dao
interface SalesReturnDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(returnData: SalesReturnEntity)

    @Query("SELECT * FROM sales_return WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<SalesReturnEntity>

    @Query("SELECT * FROM sales_return WHERE salesReturnId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: String, companyId: String, locationId: String): SalesReturnEntity?
}