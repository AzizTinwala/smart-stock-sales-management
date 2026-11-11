package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentsOutEntity

@Dao
interface PaymentsOutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentsOutEntity)

    @Query("SELECT * FROM payments_out WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<PaymentsOutEntity>

    @Query("SELECT * FROM payments_out WHERE paymentOutId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: String, companyId: String, locationId: String): PaymentsOutEntity?

    @Query("SELECT * FROM payments_out WHERE companyId = :companyId AND locationId = :locationId ORDER BY createdAt DESC")
    fun getAll(companyId: String, locationId: String): kotlinx.coroutines.flow.Flow<List<PaymentsOutEntity>>

    @Query("UPDATE payments_out SET companyId = :cid, locationId = :lid WHERE companyId = '' OR companyId IS NULL")
    suspend fun migrateUnassignedPayments(cid: String, lid: String)
}