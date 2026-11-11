package com.saifee.inventorysalesmanagement.mvvm.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuditRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.UserManagementRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManagementRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val auditRepository: AuditRepository,
    private val prefs: SharedPref
) : UserManagementRepository {

    override fun getUsersByCompany(companyId: String): Flow<List<UserProfile>> = callbackFlow {
        if (companyId.isEmpty()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        val query = firestore.collection("users").whereEqualTo("companyId", companyId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val users = snapshot?.toObjects(UserProfile::class.java) ?: emptyList()
            trySend(users)
        }

        awaitClose { listener.remove() }
    }

    override fun getAllUsers(): Flow<List<UserProfile>> = getUsersByCompany("")

    override suspend fun createUser(profile: UserProfile, password: String): Result<UserProfile> {
        return try {
            val createdUid: String = try {
                val mainApp = com.google.firebase.FirebaseApp.getInstance()
                val tempAppName = "AdminUserCreationTemp"
                val tempApp = try {
                    com.google.firebase.FirebaseApp.getInstance(tempAppName)
                } catch (e: Exception) {
                    com.google.firebase.FirebaseApp.initializeApp(mainApp.applicationContext, mainApp.options, tempAppName)
                }
                val tempAuth = com.google.firebase.auth.FirebaseAuth.getInstance(tempApp)
                val authResult = tempAuth.createUserWithEmailAndPassword(profile.email, password).await()
                val uid = authResult.user?.uid ?: profile.userId.ifEmpty { UUID.randomUUID().toString() }
                tempAuth.signOut()
                uid
            } catch (authException: Exception) {
                profile.userId.ifEmpty { UUID.randomUUID().toString() }
            }

            val newProfile = profile.copy(
                userId = createdUid,
                isApproved = true,
                isActive = true,
                updatedAt = System.currentTimeMillis().toString(),
                createdAt = System.currentTimeMillis().toString(),
                tokenVersion = 1L
            )

            firestore.collection("users").document(createdUid).set(newProfile).await()

            auditRepository.logAction(
                action = "USER_CREATED",
                targetId = createdUid,
                companyId = profile.companyId,
                details = "Created user ${profile.email} with role ${profile.role.name}"
            )

            Result.success(newProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            val updated = profile.copy(updatedAt = System.currentTimeMillis().toString())
            firestore.collection("users").document(profile.userId).set(updated).await()

            auditRepository.logAction(
                action = "USER_UPDATED",
                targetId = profile.userId,
                companyId = profile.companyId,
                details = "Updated user profile details for ${profile.email}"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setUserActiveStatus(userId: String, isActive: Boolean): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).update(
                mapOf(
                    "isActive" to isActive,
                    "updatedAt" to System.currentTimeMillis().toString()
                )
            ).await()

            val doc = firestore.collection("users").document(userId).get().await()
            val companyId = doc.getString("companyId") ?: ""

            auditRepository.logAction(
                action = if (isActive) "USER_ACTIVATED" else "USER_DEACTIVATED",
                targetId = userId,
                companyId = companyId,
                details = "Set active status of user $userId to $isActive"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun approveUser(userId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).update(
                mapOf(
                    "isApproved" to true,
                    "isActive" to true,
                    "updatedAt" to System.currentTimeMillis().toString()
                )
            ).await()

            val doc = firestore.collection("users").document(userId).get().await()
            val companyId = doc.getString("companyId") ?: ""

            auditRepository.logAction(
                action = "USER_APPROVED",
                targetId = userId,
                companyId = companyId,
                details = "Admin approved user $userId"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forceLogoutUser(userId: String): Result<Unit> {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            val currentVersion = doc.getLong("tokenVersion") ?: 1L
            val newVersion = currentVersion + 1L
            val companyId = doc.getString("companyId") ?: ""

            firestore.collection("users").document(userId).update(
                mapOf(
                    "tokenVersion" to newVersion,
                    "updatedAt" to System.currentTimeMillis().toString()
                )
            ).await()

            auditRepository.logAction(
                action = "FORCE_LOGOUT",
                targetId = userId,
                companyId = companyId,
                details = "Admin forced logout for user $userId (tokenVersion bumped to $newVersion)"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserRole(userId: String, newRole: UserRole): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).update(
                mapOf(
                    "role" to newRole.name,
                    "updatedAt" to System.currentTimeMillis().toString()
                )
            ).await()

            val doc = firestore.collection("users").document(userId).get().await()
            val companyId = doc.getString("companyId") ?: ""

            auditRepository.logAction(
                action = "USER_ROLE_CHANGED",
                targetId = userId,
                companyId = companyId,
                details = "Changed user $userId role to ${newRole.name}"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserFeaturePermissions(userId: String, featurePermissions: Map<String, Boolean>): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).update(
                mapOf(
                    "featurePermissions" to featurePermissions,
                    "updatedAt" to System.currentTimeMillis().toString()
                )
            ).await()

            val doc = firestore.collection("users").document(userId).get().await()
            val companyId = doc.getString("companyId") ?: ""

            auditRepository.logAction(
                action = "USER_PERMISSIONS_UPDATED",
                targetId = userId,
                companyId = companyId,
                details = "Updated feature permissions for user $userId"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changeUserPassword(userId: String, newPassword: String): Result<Unit> {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            val companyId = doc.getString("companyId") ?: ""
            val email = doc.getString("email") ?: ""
            val currentVersion = doc.getLong("tokenVersion") ?: 1L
            val newVersion = currentVersion + 1L

            if (auth.currentUser?.uid == userId) {
                auth.currentUser?.updatePassword(newPassword)?.await()
            } else {
                if (email.isNotEmpty()) {
                    try {
                        auth.sendPasswordResetEmail(email).await()
                    } catch (_: Exception) {}
                }
            }

            firestore.collection("users").document(userId).update(
                mapOf(
                    "tokenVersion" to newVersion,
                    "updatedAt" to System.currentTimeMillis().toString()
                )
            ).await()

            auditRepository.logAction(
                action = "USER_PASSWORD_CHANGED",
                targetId = userId,
                companyId = companyId,
                details = "Admin changed password / triggered reset for user $userId"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hashPin(pin: String): String {
        val salt = "ISM_MANAGER_PIN_SALT_v1_8A9D2F"
        val bytes = MessageDigest.getInstance("SHA-256").digest((salt + pin).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override suspend fun setManagerPin(userId: String, pin: String): Result<Unit> {
        return try {
            val hash = hashPin(pin)
            firestore.collection("users").document(userId).update(
                mapOf(
                    "managerPinHash" to hash,
                    "updatedAt" to System.currentTimeMillis().toString()
                )
            ).await()
            prefs.managerPinHash = hash

            val doc = firestore.collection("users").document(userId).get().await()
            val companyId = doc.getString("companyId") ?: ""

            auditRepository.logAction(
                action = "MANAGER_PIN_UPDATED",
                targetId = userId,
                companyId = companyId,
                details = "Updated Manager Override PIN for user $userId"
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyManagerPin(companyId: String, pin: String): Result<Boolean> {
        return try {
            val inputHash = hashPin(pin)
            if (prefs.managerPinHash.isNotEmpty() && prefs.managerPinHash == inputHash) {
                return Result.success(true)
            }

            val snapshot = firestore.collection("users")
                .whereEqualTo("companyId", companyId)
                .get()
                .await()

            val users = snapshot.toObjects(UserProfile::class.java)
            val validPin = users.any { (it.role == UserRole.ADMIN || it.role == UserRole.SUPER_USER) && it.managerPinHash == inputHash }

            if (validPin) {
                prefs.managerPinHash = inputHash
            }

            Result.success(validPin)
        } catch (e: Exception) {
            val matchLocal = prefs.managerPinHash.isNotEmpty() && prefs.managerPinHash == hashPin(pin)
            Result.success(matchLocal)
        }
    }
}
