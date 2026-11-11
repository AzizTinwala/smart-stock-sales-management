package com.saifee.inventorysalesmanagement.mvvm.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.AuditLogEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuditRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuditRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : AuditRepository {

    override suspend fun logAction(
        action: String,
        targetId: String,
        companyId: String,
        details: String,
        beforeValue: String?,
        afterValue: String?
    ) {
        try {
            val userId = auth.currentUser?.uid ?: "system"
            val log = AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                performedByUserId = userId,
                targetId = targetId,
                action = action,
                companyId = companyId,
                details = details,
                beforeValue = beforeValue,
                afterValue = afterValue,
                timestamp = System.currentTimeMillis()
            )
            firestore.collection("audit_logs").document(log.logId).set(log).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getAuditLogs(companyId: String): List<AuditLogEntity> {
        return try {
            val query = if (companyId.isEmpty()) {
                firestore.collection("audit_logs")
            } else {
                firestore.collection("audit_logs").whereEqualTo("companyId", companyId)
            }
            val snapshot = query.get().await()
            snapshot.toObjects(AuditLogEntity::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
