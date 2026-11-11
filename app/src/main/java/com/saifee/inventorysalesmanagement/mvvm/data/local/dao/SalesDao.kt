package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.relational.SalesWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sales: SalesEntity)

    @Query("SELECT * FROM sales WHERE isDeleted = 0 AND companyId = :companyId AND locationId = :locationId")
    fun getAll(companyId: String, locationId: String): Flow<List<SalesEntity>>

    @Query("SELECT * FROM sales WHERE isDeleted = 0 AND companyId = :companyId")
    fun getAllByCompany(companyId: String): Flow<List<SalesEntity>>

    @Query("SELECT * FROM sales WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<SalesEntity>

    @Query("SELECT * FROM sales WHERE salesId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: String, companyId: String, locationId: String): SalesEntity?

    @Transaction
    @Query("SELECT * FROM sales WHERE salesId = :id AND companyId = :companyId AND locationId = :locationId")
    suspend fun getSalesWithItems(id: String, companyId: String, locationId: String): SalesWithItems?

    @Query("UPDATE sales SET companyId = :cid, locationId = :lid WHERE companyId = '' OR companyId IS NULL")
    suspend fun migrateUnassignedSales(cid: String, lid: String)
}