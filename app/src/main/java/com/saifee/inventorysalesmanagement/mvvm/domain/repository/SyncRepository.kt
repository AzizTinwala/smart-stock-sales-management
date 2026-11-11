package com.saifee.inventorysalesmanagement.mvvm.domain.repository

interface SyncRepository {
    suspend fun uploadImage(parent: String, localPath: String): String?
    suspend fun downloadImage(parent: String, url: String, fileName: String): String?
    fun triggerFullSync()
    fun triggerImageSync()
    suspend fun syncAll()
    suspend fun syncImages()
    suspend fun <T : Any> pushToFirestore(
        companyId: String,
        locationId: String,
        collection: String,
        documentId: String,
        data: T
    )
    suspend fun <T : Any> pushToFirestoreWithConflictCheck(
        companyId: String,
        locationId: String,
        collection: String,
        documentId: String,
        data: T,
        localUpdatedAt: Long
    ): Result<Boolean>
}
