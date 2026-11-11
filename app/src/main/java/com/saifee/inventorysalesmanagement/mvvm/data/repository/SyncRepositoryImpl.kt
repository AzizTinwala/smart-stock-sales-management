package com.saifee.inventorysalesmanagement.mvvm.data.repository

import android.net.Uri
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.saifee.inventorysalesmanagement.mvvm.core.common_utils.LocalFileHelper
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.data.local.DataBase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.*
import com.saifee.inventorysalesmanagement.mvvm.data.worker.ImageUploadWorker
import com.saifee.inventorysalesmanagement.mvvm.data.worker.SyncWorker
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val db: DataBase,
    private val prefs: SharedPref,
    private val workManager: WorkManager,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val localFileHelper: LocalFileHelper
) : SyncRepository {

    private val selectedCompanyId: String
        get() = prefs.selectedCompanyId ?: ""

    private val selectedLocationId: String
        get() = prefs.selectedLocationId ?: ""

    private fun getTenantCollection(collection: String) =
        firestore.collection("companies").document(selectedCompanyId)
            .collection("locations").document(selectedLocationId)
            .collection(collection)

    override fun triggerFullSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncRequest)
    }

    override fun triggerImageSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncRequest)
    }

    override suspend fun syncAll() {
        if (isSuperAdmin()) {
            syncAllCompanies()
        } else {
            if (selectedCompanyId.isEmpty() || selectedLocationId.isEmpty()) {
                throw IllegalStateException("Please select a company and location before syncing.")
            }
            downloadGlobalData()
            uploadAll()
            downloadAll()
        }
    }

    private suspend fun isSuperAdmin(): Boolean {
        val uid = prefs.userId ?: FirebaseAuth.getInstance().currentUser?.uid ?: return false
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val roleStr = doc.getString("role")
            roleStr == "SUPER_USER"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun syncAllCompanies() {
        val firestoreCompanies = runCatching {
            firestore.collection("companies").get().await().toObjects(CompanyEntity::class.java)
        }.getOrDefault(emptyList())

        firestoreCompanies.forEach { company ->
            runCatching {
                db.companyDao().insert(company.copy(isSynced = true))
            }
        }

        val allCompanies = (db.companyDao().getAllList().map { it.companyId } + firestoreCompanies.map { it.companyId }).distinct()

        for (companyId in allCompanies) {
            if (companyId.isEmpty()) continue

            runCatching {
                val companyDoc = firestore.collection("companies").document(companyId).get().await()
                if (companyDoc.exists()) {
                    companyDoc.toObject(CompanyEntity::class.java)?.let {
                        db.companyDao().insert(it.copy(isSynced = true))
                    }
                }
            }

            val firestoreLocations = runCatching {
                firestore.collection("companies").document(companyId)
                    .collection("locations").get().await().toObjects(LocationEntity::class.java)
            }.getOrDefault(emptyList())

            firestoreLocations.forEach { loc ->
                runCatching {
                    db.locationDao().insert(loc.copy(isSynced = true))
                }
            }

            val localLocations = db.locationDao().getByCompanyList(companyId)
            val allLocations = (localLocations.map { it.locationId } + firestoreLocations.map { it.locationId }).distinct()

            for (locationId in allLocations) {
                if (locationId.isEmpty()) continue
                uploadForTenant(companyId, locationId)
                downloadForTenant(companyId, locationId)
            }
        }
    }

    private fun getTenantCollectionFor(companyId: String, locationId: String, collection: String) =
        firestore.collection("companies").document(companyId)
            .collection("locations").document(locationId)
            .collection(collection)

    private suspend fun uploadForTenant(companyId: String, locationId: String) {
        runCatching {
            val list = db.itemGroupDao().getUnsynced(companyId, locationId)
            syncListFor(list, getTenantCollectionFor(companyId, locationId, "item_group"), { it.groupId.toString() }, { it.isDeleted }) {
                db.itemGroupDao().insert(it.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val list = db.itemDao().getUnsynced(companyId, locationId)
            for (item in list) {
                if (item.isDeleted) {
                    getTenantCollectionFor(companyId, locationId, "item").document(item.itemId.toString()).delete().await()
                    db.itemDao().insert(item.copy(isSynced = true))
                    continue
                }
                var updatedItem = item
                if (item.imagePath?.contains("non-sync") == true) {
                    val imageUrl = uploadImage(companyId, item.imagePath)
                    if (imageUrl != null) {
                        val syncPath = localFileHelper.moveToSync(companyId, item.imagePath)
                        updatedItem = item.copy(imageUrl = imageUrl, imagePath = syncPath)
                    }
                }
                getTenantCollectionFor(companyId, locationId, "item").document(item.itemId.toString()).set(updatedItem).await()
                db.itemDao().insert(updatedItem.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val list = db.customerDao().getUnsynced(companyId, locationId)
            syncListFor(list, getTenantCollectionFor(companyId, locationId, "customer"), { it.customerId }, { it.isDeleted }) {
                db.customerDao().insert(it.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val list = db.vendorDao().getUnsynced(companyId, locationId)
            syncListFor(list, getTenantCollectionFor(companyId, locationId, "vendor"), { it.vendorId }, { it.isDeleted }) {
                db.vendorDao().insert(it.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val list = db.salesDao().getUnsynced(companyId, locationId)
            syncListFor(list, getTenantCollectionFor(companyId, locationId, "sales"), { it.salesId }, { it.isDeleted }) {
                db.salesDao().insert(it.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val list = db.salesItemDao().getUnsynced(companyId, locationId)
            syncListFor(list, getTenantCollectionFor(companyId, locationId, "sales_item"), { it.salesItemId }, { false }) {
                db.salesItemDao().insert(it.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val list = db.purchaseDao().getUnsynced(companyId, locationId)
            syncListFor(list, getTenantCollectionFor(companyId, locationId, "purchase"), { it.purchaseId }, { it.isDeleted }) {
                db.purchaseDao().insert(it.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val list = db.purchaseItemDao().getUnsynced(companyId, locationId)
            syncListFor(list, getTenantCollectionFor(companyId, locationId, "purchase_item"), { it.purchaseItemId }, { false }) {
                db.purchaseItemDao().insert(it.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val listIn = db.paymentReceivedDao().getUnsynced(companyId, locationId)
            syncListFor(listIn, getTenantCollectionFor(companyId, locationId, "payment_received"), { it.paymentReceivedId }, { false }) {
                db.paymentReceivedDao().insert(it.copy(isSynced = true))
            }
            val listOut = db.paymentsOutDao().getUnsynced(companyId, locationId)
            syncListFor(listOut, getTenantCollectionFor(companyId, locationId, "payments_out"), { it.paymentOutId }, { false }) {
                db.paymentsOutDao().insert(it.copy(isSynced = true))
            }
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun downloadForTenant(companyId: String, locationId: String) {
        val tenantCol = { col: String -> getTenantCollectionFor(companyId, locationId, col) }

        runCatching {
            val remoteList = tenantCol("item_group").get().await().toObjects(ItemGroupEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.itemGroupDao().getById(remote.groupId, companyId, locationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.itemGroupDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteList = tenantCol("item").get().await().toObjects(ItemEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.itemDao().getById(remote.itemId, companyId, locationId)
                var itemToSave = remote.copy(isSynced = true)
                if (remote.imageUrl != null) {
                    val fileName = remote.imageUrl.substringAfterLast("/").substringBefore("?").split("%2F").last()
                    if (!localFileHelper.existsInSync(companyId, fileName)) {
                        val localPath = downloadImage(companyId, remote.imageUrl, fileName)
                        if (localPath != null) {
                            itemToSave = itemToSave.copy(imagePath = localPath)
                        }
                    } else {
                        itemToSave = itemToSave.copy(imagePath = localFileHelper.getSyncFile(companyId, fileName).absolutePath)
                    }
                }
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.itemDao().insert(itemToSave)
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteList = tenantCol("customer").get().await().toObjects(CustomerEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.customerDao().getById(remote.customerId, companyId, locationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.customerDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteList = tenantCol("vendor").get().await().toObjects(VendorEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.vendorDao().getById(remote.vendorId, companyId, locationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.vendorDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteList = tenantCol("sales").get().await().toObjects(SalesEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.salesDao().getById(remote.salesId, companyId, locationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.salesDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteList = tenantCol("purchase").get().await().toObjects(PurchaseEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.purchaseDao().getById(remote.purchaseId, companyId, locationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.purchaseDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteList = tenantCol("sales_item").get().await().toObjects(SalesItemEntity::class.java)
            val now = System.currentTimeMillis()
            val userId = prefs.userId ?: "SYSTEM"
            remoteList.forEach { remote ->
                val existing = db.salesItemDao().getBySalesId(remote.salesId, companyId, locationId)
                    .find { it.salesItemId == remote.salesItemId }
                val localSale = db.salesDao().getById(remote.salesId, companyId, locationId)
                val isLocallyCreated = localSale != null && localSale.createdBy == userId

                if (existing == null) {
                    db.salesItemDao().insert(remote.copy(isSynced = true))
                    if (!isLocallyCreated) {
                        db.itemDao().updateStock(remote.itemId, -remote.quantity, now, userId, companyId, locationId)
                    }
                } else if (remote.updatedAt > existing.updatedAt) {
                    val qtyDelta = remote.quantity - existing.quantity
                    db.salesItemDao().insert(remote.copy(isSynced = true))
                    if (qtyDelta != 0.0) {
                        db.itemDao().updateStock(remote.itemId, -qtyDelta, now, userId, companyId, locationId)
                    }
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteList = tenantCol("purchase_item").get().await().toObjects(PurchaseItemEntity::class.java)
            val now = System.currentTimeMillis()
            val userId = prefs.userId ?: "SYSTEM"
            remoteList.forEach { remote ->
                val existing = db.purchaseItemDao().getByPurchaseId(remote.purchaseId, companyId, locationId)
                    .find { it.purchaseItemId == remote.purchaseItemId }
                val localPurchase = db.purchaseDao().getById(remote.purchaseId, companyId, locationId)
                val isLocallyCreated = localPurchase != null && localPurchase.createdBy == userId

                if (existing == null) {
                    db.purchaseItemDao().insert(remote.copy(isSynced = true))
                    if (!isLocallyCreated) {
                        db.itemDao().updateStock(remote.itemId, remote.quantity, now, userId, companyId, locationId)
                    }
                } else if (remote.updatedAt > existing.updatedAt) {
                    val qtyDelta = remote.quantity - existing.quantity
                    db.purchaseItemDao().insert(remote.copy(isSynced = true))
                    if (qtyDelta != 0.0) {
                        db.itemDao().updateStock(remote.itemId, qtyDelta, now, userId, companyId, locationId)
                    }
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteIn = tenantCol("payment_received").get().await().toObjects(PaymentReceivedEntity::class.java)
            remoteIn.forEach { remote ->
                val local = db.paymentReceivedDao().getById(remote.paymentReceivedId, companyId, locationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.paymentReceivedDao().insert(remote.copy(isSynced = true))
                }
            }
            val remoteOut = tenantCol("payments_out").get().await().toObjects(PaymentsOutEntity::class.java)
            remoteOut.forEach { remote ->
                val local = db.paymentsOutDao().getById(remote.paymentOutId, companyId, locationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.paymentsOutDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun <T> syncListFor(
        list: List<T>,
        collectionRef: com.google.firebase.firestore.CollectionReference,
        getId: (T) -> String,
        isDeleted: (T) -> Boolean,
        markSynced: suspend (T) -> Unit
    ) {
        for (item in list) {
            try {
                val doc = collectionRef.document(getId(item))
                if (isDeleted(item)) {
                    doc.delete().await()
                } else {
                    doc.set(item as Any).await()
                }
                markSynced(item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun syncImages() {
        if (selectedCompanyId.isNotEmpty() && selectedLocationId.isNotEmpty()) {
            uploadItems() // uploadItems handles image uploads
        }
    }

    override suspend fun <T : Any> pushToFirestore(
        companyId: String,
        locationId: String,
        collection: String,
        documentId: String,
        data: T
    ) {
        try {
            firestore.collection("companies").document(companyId)
                .collection("locations").document(locationId)
                .collection(collection).document(documentId).set(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
            triggerFullSync()
        }
    }

    override suspend fun <T : Any> pushToFirestoreWithConflictCheck(
        companyId: String,
        locationId: String,
        collection: String,
        documentId: String,
        data: T,
        localUpdatedAt: Long
    ): Result<Boolean> {
        return try {
            val docRef = firestore.collection("companies").document(companyId)
                .collection("locations").document(locationId)
                .collection(collection).document(documentId)

            val remoteDoc = docRef.get().await()
            if (remoteDoc.exists()) {
                val remoteUpdatedAt = remoteDoc.getLong("updatedAt") ?: 0L
                if (remoteUpdatedAt > localUpdatedAt) {
                    return Result.success(false)
                }
            }

            docRef.set(data).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun downloadGlobalData() {
        if (selectedCompanyId.isEmpty()) return
        runCatching {
            // Sync Company for active tenant
            val companyDoc = firestore.collection("companies").document(selectedCompanyId).get().await()
            if (companyDoc.exists()) {
                val remote = companyDoc.toObject(CompanyEntity::class.java)
                if (remote != null) {
                    val local = db.companyDao().getById(remote.companyId)
                    if (local == null || remote.updatedAt > local.updatedAt) {
                        db.companyDao().insert(remote.copy(isSynced = true))
                    }
                }
            }
        }.onFailure { it.printStackTrace() }

        // Sync Locations for active company
        if (selectedLocationId.isNotEmpty()) {
            runCatching {
                val locations = firestore.collection("companies").document(selectedCompanyId)
                    .collection("locations").get().await().toObjects(LocationEntity::class.java)
                locations.forEach { remote ->
                    val local = db.locationDao().getById(remote.locationId)
                    if (local == null || remote.updatedAt > local.updatedAt) {
                        db.locationDao().insert(remote.copy(isSynced = true))
                    }
                }
            }.onFailure { it.printStackTrace() }
        }
    }

    private suspend fun uploadAll() {
        uploadItemGroups()
        uploadItems()
        uploadCustomers()
        uploadVendors()
        uploadSales()
        uploadSalesItems()
        uploadPurchases()
        uploadPurchaseItems()
        uploadPayments()
    }

    private suspend fun downloadAll() {
        downloadItemGroups()
        downloadItems()
        downloadCustomers()
        downloadVendors()
        downloadSales()
        downloadSalesItems()
        downloadPurchases()
        downloadPurchaseItems()
        downloadPayments()
    }

    private suspend fun downloadItemGroups() {
        runCatching {
            val remoteList = getTenantCollection("item_group").get().await().toObjects(ItemGroupEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.itemGroupDao().getById(remote.groupId, selectedCompanyId, selectedLocationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.itemGroupDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun downloadItems() {
        runCatching {
            val remoteList = getTenantCollection("item").get().await().toObjects(ItemEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.itemDao().getById(remote.itemId, selectedCompanyId, selectedLocationId)
                
                var itemToSave = remote.copy(isSynced = true)
                
                if (remote.imageUrl != null) {
                    val fileName = remote.imageUrl.substringAfterLast("/").substringBefore("?").split("%2F").last()
                    if (!localFileHelper.existsInSync(selectedCompanyId, fileName)) {
                        val localPath = downloadImage(selectedCompanyId, remote.imageUrl, fileName)
                        if (localPath != null) {
                            itemToSave = itemToSave.copy(imagePath = localPath)
                        }
                    } else {
                        itemToSave = itemToSave.copy(imagePath = localFileHelper.getSyncFile(selectedCompanyId, fileName).absolutePath)
                    }
                }

                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.itemDao().insert(itemToSave)
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    override suspend fun downloadImage(parent: String, url: String, fileName: String): String? {
        return try {
            val storageRef = storage.getReferenceFromUrl(url)
            val file = localFileHelper.getSyncFile(parent, fileName)
            storageRef.getFile(file).await()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun downloadCustomers() {
        runCatching {
            val remoteList = getTenantCollection("customer").get().await().toObjects(CustomerEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.customerDao().getById(remote.customerId, selectedCompanyId, selectedLocationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.customerDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun downloadVendors() {
        runCatching {
            val remoteList = getTenantCollection("vendor").get().await().toObjects(VendorEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.vendorDao().getById(remote.vendorId, selectedCompanyId, selectedLocationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.vendorDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun downloadSales() {
        runCatching {
            val remoteList = getTenantCollection("sales").get().await().toObjects(SalesEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.salesDao().getById(remote.salesId, selectedCompanyId, selectedLocationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.salesDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun downloadPurchases() {
        runCatching {
            val remoteList = getTenantCollection("purchase").get().await().toObjects(PurchaseEntity::class.java)
            remoteList.forEach { remote ->
                val local = db.purchaseDao().getById(remote.purchaseId, selectedCompanyId, selectedLocationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.purchaseDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun downloadSalesItems() {
        val remoteList = runCatching {
            getTenantCollection("sales_item").get().await().toObjects(SalesItemEntity::class.java)
        }.getOrDefault(emptyList())

        val now = System.currentTimeMillis()
        val userId = prefs.userId ?: "SYSTEM"
        remoteList.forEach { remote ->
            val existing = db.salesItemDao().getBySalesId(remote.salesId, selectedCompanyId, selectedLocationId)
                .find { it.salesItemId == remote.salesItemId }
            val localSale = db.salesDao().getById(remote.salesId, selectedCompanyId, selectedLocationId)
            val isLocallyCreated = localSale != null && localSale.createdBy == userId

            if (existing == null) {
                db.salesItemDao().insert(remote.copy(isSynced = true))
                if (!isLocallyCreated) {
                    db.itemDao().updateStock(
                        remote.itemId,
                        -remote.quantity,
                        now,
                        userId,
                        selectedCompanyId,
                        selectedLocationId
                    )
                }
            } else if (remote.updatedAt > existing.updatedAt) {
                val qtyDelta = remote.quantity - existing.quantity
                db.salesItemDao().insert(remote.copy(isSynced = true))
                if (qtyDelta != 0.0) {
                    db.itemDao().updateStock(
                        remote.itemId,
                        -qtyDelta,
                        now,
                        userId,
                        selectedCompanyId,
                        selectedLocationId
                    )
                }
            }
        }
    }

    private suspend fun downloadPurchaseItems() {
        val remoteList = runCatching {
            getTenantCollection("purchase_item").get().await().toObjects(PurchaseItemEntity::class.java)
        }.getOrDefault(emptyList())

        val now = System.currentTimeMillis()
        val userId = prefs.userId ?: "SYSTEM"
        remoteList.forEach { remote ->
            val existing = db.purchaseItemDao().getByPurchaseId(remote.purchaseId, selectedCompanyId, selectedLocationId)
                .find { it.purchaseItemId == remote.purchaseItemId }
            val localPurchase = db.purchaseDao().getById(remote.purchaseId, selectedCompanyId, selectedLocationId)
            val isLocallyCreated = localPurchase != null && localPurchase.createdBy == userId

            if (existing == null) {
                db.purchaseItemDao().insert(remote.copy(isSynced = true))
                if (!isLocallyCreated) {
                    db.itemDao().updateStock(
                        remote.itemId,
                        remote.quantity,
                        now,
                        userId,
                        selectedCompanyId,
                        selectedLocationId
                    )
                }
            } else if (remote.updatedAt > existing.updatedAt) {
                val qtyDelta = remote.quantity - existing.quantity
                db.purchaseItemDao().insert(remote.copy(isSynced = true))
                if (qtyDelta != 0.0) {
                    db.itemDao().updateStock(
                        remote.itemId,
                        qtyDelta,
                        now,
                        userId,
                        selectedCompanyId,
                        selectedLocationId
                    )
                }
            }
        }
    }

    private suspend fun downloadPayments() {
        runCatching {
            val remoteIn = getTenantCollection("payment_received").get().await().toObjects(PaymentReceivedEntity::class.java)
            remoteIn.forEach { remote ->
                val local = db.paymentReceivedDao().getById(remote.paymentReceivedId, selectedCompanyId, selectedLocationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.paymentReceivedDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }

        runCatching {
            val remoteOut = getTenantCollection("payments_out").get().await().toObjects(PaymentsOutEntity::class.java)
            remoteOut.forEach { remote ->
                val local = db.paymentsOutDao().getById(remote.paymentOutId, selectedCompanyId, selectedLocationId)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    db.paymentsOutDao().insert(remote.copy(isSynced = true))
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun uploadItemGroups() {
        val list = db.itemGroupDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(list, "item_group", { it.groupId.toString() }, { it.isDeleted }) {
            db.itemGroupDao().insert(it.copy(isSynced = true))
        }
    }

    private suspend fun uploadItems() {
        val list = db.itemDao().getUnsynced(selectedCompanyId, selectedLocationId)
        for (item in list) {
            if (item.isDeleted) {
                getTenantCollection("item").document(item.itemId.toString()).delete().await()
                db.itemDao().insert(item.copy(isSynced = true))
                continue
            }

            var updatedItem = item

            if (item.imagePath?.contains("non-sync") == true) {
                val imageUrl = uploadImage(selectedCompanyId, item.imagePath)
                if (imageUrl != null) {
                    val syncPath = localFileHelper.moveToSync(selectedCompanyId, item.imagePath)
                    updatedItem = item.copy(imageUrl = imageUrl, imagePath = syncPath)
                }
            }

            getTenantCollection("item").document(item.itemId.toString()).set(updatedItem).await()
            db.itemDao().insert(updatedItem.copy(isSynced = true))
        }
    }

    override suspend fun uploadImage(parent: String, localPath: String): String? {
        val file = File(localPath)
        if (!file.exists()) return null

        return try {
            val storageRef = storage.reference.child("${parent}/images/${file.name}")
            storageRef.putFile(Uri.fromFile(file)).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun uploadCustomers() {
        val list = db.customerDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(list, "customer", { it.customerId }, { it.isDeleted }) {
            db.customerDao().insert(it.copy(isSynced = true))
        }
    }

    private suspend fun uploadVendors() {
        val list = db.vendorDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(list, "vendor", { it.vendorId }, { it.isDeleted }) {
            db.vendorDao().insert(it.copy(isSynced = true))
        }
    }

    private suspend fun uploadSales() {
        val list = db.salesDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(list, "sales", { it.salesId }, { it.isDeleted }) {
            db.salesDao().insert(it.copy(isSynced = true))
        }
    }

    private suspend fun uploadSalesItems() {
        val list = db.salesItemDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(list, "sales_item", { it.salesItemId }, { false }) {
            db.salesItemDao().insert(it.copy(isSynced = true))
        }
    }

    private suspend fun uploadPurchases() {
        val list = db.purchaseDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(list, "purchase", { it.purchaseId }, { it.isDeleted }) {
            db.purchaseDao().insert(it.copy(isSynced = true))
        }
    }

    private suspend fun uploadPurchaseItems() {
        val list = db.purchaseItemDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(list, "purchase_item", { it.purchaseItemId }, { false }) {
            db.purchaseItemDao().insert(it.copy(isSynced = true))
        }
    }

    private suspend fun uploadPayments() {
        val listIn = db.paymentReceivedDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(listIn, "payment_received", { it.paymentReceivedId }, { false }) {
            db.paymentReceivedDao().insert(it.copy(isSynced = true))
        }

        val listOut = db.paymentsOutDao().getUnsynced(selectedCompanyId, selectedLocationId)
        syncList(listOut, "payments_out", { it.paymentOutId }, { false }) {
            db.paymentsOutDao().insert(it.copy(isSynced = true))
        }
    }

    private suspend fun <T> syncList(
        list: List<T>,
        collection: String,
        getId: (T) -> String,
        isDeleted: (T) -> Boolean,
        markSynced: suspend (T) -> Unit
    ) {
        for (item in list) {
            try {
                val doc = getTenantCollection(collection).document(getId(item))
                if (isDeleted(item)) {
                    doc.delete().await()
                } else {
                    doc.set(item as Any).await()
                }
                markSynced(item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
