package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentReceivedEntity

@Dao
interface PaymentReceivedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentReceivedEntity)

    @Query("SELECT * FROM payment_received WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<PaymentReceivedEntity>

    @Query("SELECT * FROM payment_received WHERE paymentReceivedId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: String, companyId: String, locationId: String): PaymentReceivedEntity?

    @Query("SELECT * FROM payment_received WHERE companyId = :companyId AND locationId = :locationId ORDER BY createdAt DESC")
    fun getAll(companyId: String, locationId: String): kotlinx.coroutines.flow.Flow<List<PaymentReceivedEntity>>

    @Query("UPDATE payment_received SET companyId = :cid, locationId = :lid WHERE companyId = '' OR companyId IS NULL")
    suspend fun migrateUnassignedPayments(cid: String, lid: String)
}