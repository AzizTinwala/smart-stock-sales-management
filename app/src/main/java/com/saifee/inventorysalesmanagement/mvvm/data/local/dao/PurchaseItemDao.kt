package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseItemEntity

@Dao
interface PurchaseItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PurchaseItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PurchaseItemEntity>): List<Long>

    @Query("SELECT * FROM purchase_item WHERE purchaseId = :purchaseId AND companyId = :companyId AND locationId = :locationId")
    suspend fun getByPurchaseId(purchaseId: String, companyId: String, locationId: String): List<PurchaseItemEntity>

    @Query("SELECT * FROM purchase_item WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<PurchaseItemEntity>

    @Query("DELETE FROM purchase_item WHERE purchaseId = :purchaseId AND companyId = :companyId AND locationId = :locationId")
    suspend fun deleteByPurchaseId(purchaseId: String, companyId: String, locationId: String)
}