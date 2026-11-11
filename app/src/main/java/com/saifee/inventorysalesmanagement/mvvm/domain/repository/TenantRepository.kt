package com.saifee.inventorysalesmanagement.mvvm.domain.repository

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

interface TenantRepository {
    val selectedCompanyIdFlow: Flow<String?>
    val selectedLocationIdFlow: Flow<String?>

    fun getCompanies(): Flow<List<CompanyEntity>>
    suspend fun getCompanyById(companyId: String): CompanyEntity?
    fun getLocations(companyId: String): Flow<List<LocationEntity>>
    suspend fun addCompany(company: CompanyEntity)
    suspend fun addLocation(location: LocationEntity)
    suspend fun deleteCompany(company: CompanyEntity)
    suspend fun deleteLocation(location: LocationEntity)
    fun updateSelectedCompany(companyId: String?)
    fun updateSelectedLocation(locationId: String?)

    // Helper methods for other repositories
    fun getSelectedCompanyId(): String
    fun getSelectedLocationId(): String
    fun getSelectedUserId(): String
}
