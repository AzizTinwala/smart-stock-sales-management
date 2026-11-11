package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemGroupEntity): Long

    @Query("SELECT * FROM item_group WHERE isDeleted = 0 AND companyId = :companyId AND locationId = :locationId")
    fun getAll(companyId: String, locationId: String): Flow<List<ItemGroupEntity>>

    @Query("SELECT * FROM item_group WHERE isSynced = 0 AND companyId = :companyId AND locationId = :locationId")
    suspend fun getUnsynced(companyId: String, locationId: String): List<ItemGroupEntity>

    @Query("SELECT * FROM item_group WHERE groupId = :id AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getById(id: Long, companyId: String, locationId: String): ItemGroupEntity?

    @Query("SELECT * FROM item_group WHERE groupName = :name AND companyId = :companyId AND locationId = :locationId LIMIT 1")
    suspend fun getByName(name: String, companyId: String, locationId: String): ItemGroupEntity?

    @Update
    suspend fun update(item: ItemGroupEntity)

    @Query("UPDATE item_group SET companyId = :cid, locationId = :lid WHERE companyId = '' OR companyId IS NULL")
    suspend fun migrateUnassignedGroups(cid: String, lid: String)
}