package com.saifee.inventorysalesmanagement.mvvm.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ActivityLogDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.CompanyDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ItemGroupDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.LocationDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PaymentReceivedDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PaymentsOutDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PurchaseDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.LocationEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TenantRepositoryImpl @Inject constructor(
    private val prefs: SharedPref,
    private val companyDao: CompanyDao,
    private val locationDao: LocationDao,
    private val itemDao: ItemDao,
    private val itemGroupDao: ItemGroupDao,
    private val salesDao: SalesDao,
    private val purchaseDao: PurchaseDao,
    private val activityLogDao: ActivityLogDao,
    private val paymentReceivedDao: PaymentReceivedDao,
    private val paymentsOutDao: PaymentsOutDao,
    private val firestore: FirebaseFirestore,
    private val syncRepository: SyncRepository
) : TenantRepository {

    private val _selectedCompanyId = MutableStateFlow(prefs.selectedCompanyId)
    override val selectedCompanyIdFlow: Flow<String?> = _selectedCompanyId

    private val _selectedLocationId = MutableStateFlow(prefs.selectedLocationId)
    override val selectedLocationIdFlow: Flow<String?> = _selectedLocationId

    private suspend fun triggerSyncAndBackup() {
        try {
            syncRepository.syncAll()
        } catch (e: Exception) {
            e.printStackTrace()
            syncRepository.triggerFullSync()
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val companies = companyDao.getAllList()

            if (companies.isEmpty()) {
                updateSelectedCompany(null)
                updateSelectedLocation(null)
            } else {
                val companyToUse = prefs.selectedCompanyId ?: companies.first().companyId
                val locationToUse = prefs.selectedLocationId ?: locationDao.getByCompanyList(companyToUse).firstOrNull()?.locationId ?: ""
                
                updateSelectedCompany(companyToUse)
                updateSelectedLocation(locationToUse)

                // Migration logic: Assign any unassigned data to the active company/location
                if (companyToUse.isNotEmpty() && locationToUse.isNotEmpty()) {
                    itemDao.migrateUnassignedItems(companyToUse, locationToUse)
                    itemGroupDao.migrateUnassignedGroups(companyToUse, locationToUse)
                    salesDao.migrateUnassignedSales(companyToUse, locationToUse)
                    purchaseDao.migrateUnassignedPurchases(companyToUse, locationToUse)
                    activityLogDao.migrateUnassignedLogs(companyToUse, locationToUse)
                    paymentReceivedDao.migrateUnassignedPayments(companyToUse, locationToUse)
                    paymentsOutDao.migrateUnassignedPayments(companyToUse, locationToUse)
                }
            }
        }
    }

    override fun getCompanies(): Flow<List<CompanyEntity>> = companyDao.getAll()

    override suspend fun getCompanyById(companyId: String): CompanyEntity? = companyDao.getById(companyId)

    override fun getLocations(companyId: String): Flow<List<LocationEntity>> =
        locationDao.getByCompany(companyId)

    override suspend fun addCompany(company: CompanyEntity) {
        val updated = company.copy(
            isSynced = false,
            createdBy = if (company.createdBy.isEmpty()) getSelectedUserId() else company.createdBy,
            updatedBy = getSelectedUserId()
        )
        companyDao.insert(updated)
        try {
            firestore.collection("companies").document(updated.companyId).set(updated.copy(isSynced = true)).await()
            companyDao.insert(updated.copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Automatically create one location named "Main Branch" for every new company
        val existingLocations = locationDao.getByCompanyList(updated.companyId)
        if (existingLocations.isEmpty()) {
            val now = System.currentTimeMillis()
            val userId = getSelectedUserId()
            val defaultLocation = LocationEntity(
                locationId = "LOC_${UUID.randomUUID()}",
                companyId = updated.companyId,
                locationName = "Main Branch",
                createdAt = now,
                updatedAt = now,
                createdBy = userId,
                updatedBy = userId,
                isSynced = false
            )
            addLocation(defaultLocation)
        }

        triggerSyncAndBackup()
    }

    override suspend fun addLocation(location: LocationEntity) {
        val updated = location.copy(
            isSynced = false,
            createdBy = if (location.createdBy.isEmpty()) getSelectedUserId() else location.createdBy,
            updatedBy = getSelectedUserId()
        )
        locationDao.insert(updated)
        try {
            firestore.collection("companies").document(updated.companyId)
                .collection("locations").document(updated.locationId).set(updated.copy(isSynced = true)).await()
            locationDao.insert(updated.copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        triggerSyncAndBackup()
    }

    override suspend fun deleteCompany(company: CompanyEntity) {
        val updated = company.copy(
            isDeleted = true,
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            updatedBy = getSelectedUserId()
        )
        companyDao.insert(updated)
        try {
            firestore.collection("companies").document(updated.companyId).set(updated.copy(isSynced = true)).await()
            companyDao.insert(updated.copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        triggerSyncAndBackup()
    }

    override suspend fun deleteLocation(location: LocationEntity) {
        val updated = location.copy(
            isDeleted = true,
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            updatedBy = getSelectedUserId()
        )
        locationDao.insert(updated)
        try {
            firestore.collection("companies").document(updated.companyId)
                .collection("locations").document(updated.locationId).set(updated.copy(isSynced = true)).await()
            locationDao.insert(updated.copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        triggerSyncAndBackup()
    }

    override fun updateSelectedCompany(companyId: String?) {
        prefs.selectedCompanyId = companyId
        _selectedCompanyId.value = companyId
    }

    override fun updateSelectedLocation(locationId: String?) {
        prefs.selectedLocationId = locationId
        _selectedLocationId.value = locationId
    }

    override fun getSelectedCompanyId(): String = _selectedCompanyId.value ?: ""

    override fun getSelectedLocationId(): String = _selectedLocationId.value ?: ""

    override fun getSelectedUserId(): String = prefs.userId ?: "unknown"
}
