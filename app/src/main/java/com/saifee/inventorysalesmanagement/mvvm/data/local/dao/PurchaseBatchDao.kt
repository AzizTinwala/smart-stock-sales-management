package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseBatchEntity

@Dao
interface PurchaseBatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(batch: PurchaseBatchEntity): Long

    @Query("SELECT * FROM purchase_batch WHERE itemId = :itemId AND quantityRemaining > 0 AND isDeleted = 0 AND companyId = :companyId AND locationId = :locationId ORDER BY createdAt ASC")
    suspend fun getAvailableBatchesForItem(itemId: Long, companyId: String, locationId: String): List<PurchaseBatchEntity>

    @Query("UPDATE purchase_batch SET quantityRemaining = :remaining, updatedAt = :updatedAt, isSynced = 0 WHERE batchId = :batchId")
    suspend fun updateRemainingQuantity(batchId: Long, remaining: Double, updatedAt: Long)
}
