package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity)

    @Query("SELECT * FROM location WHERE isDeleted = 0 AND companyId = :companyId")
    fun getByCompany(companyId: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM location WHERE locationId = :id LIMIT 1")
    suspend fun getById(id: String): LocationEntity?

    @Query("SELECT * FROM location WHERE isDeleted = 0 AND companyId = :companyId")
    suspend fun getByCompanyList(companyId: String): List<LocationEntity>
}
