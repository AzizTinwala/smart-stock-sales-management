package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {

    @Insert
    suspend fun insertLog(log: ActivityLogEntity)

    @Query("SELECT * FROM activity_logs WHERE companyId = :companyId AND locationId = :locationId ORDER BY createdAt DESC")
    fun getLogs(companyId: String, locationId: String): Flow<List<ActivityLogEntity>>

    @Query("DELETE FROM activity_logs WHERE companyId = :companyId AND locationId = :locationId")
    suspend fun clearLogs(companyId: String, locationId: String)

    @Query("UPDATE activity_logs SET companyId = :cid, locationId = :lid WHERE companyId = '' OR companyId IS NULL")
    suspend fun migrateUnassignedLogs(cid: String, lid: String)
}