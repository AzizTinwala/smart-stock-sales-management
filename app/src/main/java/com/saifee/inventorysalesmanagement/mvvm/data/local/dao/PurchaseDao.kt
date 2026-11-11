package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.relational.PurchaseWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchase WHERE isDeleted = 0 AND companyId = :companyId AND locationId = :locationId")
    fun getAll(companyId: String, locationId: String): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchase WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<PurchaseEntity>

    @Query("SELECT * FROM purchase WHERE purchaseId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: String, companyId: String, locationId: String): PurchaseEntity?

    @Transaction
    @Query("SELECT * FROM purchase WHERE purchaseId = :id AND companyId = :companyId AND locationId = :locationId")
    suspend fun getPurchaseWithItems(id: String, companyId: String, locationId: String): PurchaseWithItems?

    @Query("UPDATE purchase SET companyId = :cid, locationId = :lid WHERE companyId = '' OR companyId IS NULL")
    suspend fun migrateUnassignedPurchases(cid: String, lid: String)
}
