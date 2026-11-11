package com.saifee.inventorysalesmanagement.mvvm.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSyncHelper @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun getTenantCollection(companyId: String, locationId: String, collection: String) =
        firestore.collection("companies").document(companyId)
            .collection("locations").document(locationId)
            .collection(collection)

    suspend fun <T : Any> push(
        companyId: String,
        locationId: String,
        collection: String,
        documentId: String,
        data: T
    ) {
        if (companyId.isEmpty() || locationId.isEmpty()) return
        getTenantCollection(companyId, locationId, collection).document(documentId).set(data).await()
    }

    suspend fun <T : Any> fetchAll(
        companyId: String,
        locationId: String,
        collection: String,
        clazz: Class<T>
    ): List<T> {
        if (companyId.isEmpty() || locationId.isEmpty()) return emptyList()
        return getTenantCollection(companyId, locationId, collection).get().await().toObjects(clazz)
    }

    fun <T : Any> observeCollection(
        companyId: String,
        locationId: String,
        collection: String,
        clazz: Class<T>,
        onUpdate: (List<T>) -> Unit
    ) {
        if (companyId.isEmpty() || locationId.isEmpty()) return
        getTenantCollection(companyId, locationId, collection).addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                onUpdate(it.toObjects(clazz))
            }
        }
    }
}
