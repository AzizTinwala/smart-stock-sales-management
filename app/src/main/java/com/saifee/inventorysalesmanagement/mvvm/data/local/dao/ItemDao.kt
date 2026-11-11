package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.paging.PagingSource
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity): Long

    @Query("SELECT * FROM item WHERE isDeleted = 0 AND companyId = :companyId AND locationId = :locationId")
    fun getAll(companyId: String, locationId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM item WHERE isDeleted = 0 AND companyId = :companyId AND locationId = :locationId ORDER BY itemName ASC")
    fun pagingSource(companyId: String, locationId: String): PagingSource<Int, ItemEntity>

    @Query("SELECT * FROM item WHERE isDeleted = 0 AND companyId = :companyId")
    fun getAllByCompany(companyId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM item WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<ItemEntity>

    @Query("SELECT * FROM item WHERE itemId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: Long, companyId: String, locationId: String): ItemEntity?

    @Query("SELECT * FROM item WHERE itemId IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<ItemEntity>

    @Query("UPDATE item SET stockQty = stockQty + :quantity, updatedAt = :updatedAt, updatedBy = :updatedBy, isSynced = 0 WHERE itemId = :itemId AND companyId = :companyId AND locationId = :locationId")
    suspend fun updateStock(itemId: Long, quantity: Double, updatedAt: Long, updatedBy: String, companyId: String, locationId: String)

    @Query("SELECT * FROM item WHERE groupId = :groupId AND companyId = :companyId AND locationId = :locationId")
    fun getByGroup(groupId: Long, companyId: String, locationId: String): Flow<List<ItemEntity>>

    @Query("DELETE FROM item WHERE companyId = :companyId AND locationId = :locationId")
    suspend fun clearAll(companyId: String, locationId: String)

    @Query("UPDATE item SET companyId = :cid, locationId = :lid WHERE companyId = '' OR companyId IS NULL")
    suspend fun migrateUnassignedItems(cid: String, lid: String)
}