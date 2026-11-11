package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: CustomerEntity)

    @Query("SELECT * FROM customer WHERE companyId = :companyId AND locationId = :locationId")
    fun getAll(companyId: String, locationId: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customer WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<CustomerEntity>

    @Query("SELECT * FROM customer WHERE customerId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: String, companyId: String, locationId: String): CustomerEntity?
}