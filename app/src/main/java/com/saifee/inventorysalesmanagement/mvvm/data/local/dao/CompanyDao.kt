package com.saifee.inventorysalesmanagement.mvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(company: CompanyEntity)

    @Query("SELECT * FROM company WHERE isDeleted = 0")
    fun getAll(): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM company WHERE companyId = :id LIMIT 1")
    suspend fun getById(id: String): CompanyEntity?
    @Query("SELECT * FROM company WHERE isDeleted = 0")
    suspend fun getAllList(): List<CompanyEntity>
}
