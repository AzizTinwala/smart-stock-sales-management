package com.saifee.inventorysalesmanagement.mvvm.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.LocationDao
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val prefs: SharedPref,
    private val locationDao: LocationDao,
    private val syncRepository: SyncRepository
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User UID is null")

            val doc = firestore.collection("users").document(uid).get().await()

            val profile = doc.toObject(UserProfile::class.java) ?: UserProfile(
                userId = uid,
                email = email,
                name = email.substringBefore("@"),
                role = UserRole.USER,
                isApproved = false
            )

            if (!profile.isApproved && profile.role != UserRole.SUPER_USER) {
                return Result.failure(Exception("Account is pending admin approval."))
            }

            prefs.userId = uid
            prefs.selectedCompanyId = profile.companyId
            prefs.tokenVersion = profile.tokenVersion

            // Determine 1st location for the company and save in SharedPref
            val companyId = profile.companyId
            if (companyId.isNotEmpty()) {
                var locationId: String? = null
                val localLocations = locationDao.getByCompanyList(companyId)
                val firstLocal = localLocations.firstOrNull()

                if (localLocations.isNotEmpty()) {
                    locationId = localLocations.firstOrNull()?.locationId
                } else {
                    try {
                        val locsQuery = firestore.collection("companies").document(companyId)
                            .collection("locations").get().await()
                        val firstRemote = locsQuery.documents.firstOrNull()

                        locationId = firstRemote?.getString("locationId") ?: firstRemote?.id
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                prefs.selectedLocationId = locationId
            }

            // Start auto first sync after login
            syncRepository.triggerFullSync()

            Result.success(profile)
        } catch (e: Exception) {
            Log.e("AuthDebug", "Login exception", e)
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        pass: String,
        name: String,
        companyId: String,
        phone: String
    ): Result<UserProfile> {
        return try {
            val existingPhone = firestore.collection("users")
                .whereEqualTo("phone", phone)
                .get()
                .await()
            if (!existingPhone.isEmpty) {
                return Result.failure(Exception("Mobile number is already registered."))
            }

            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User UID is null")

            val profile = UserProfile(
                userId = uid,
                email = email,
                name = name,
                phone = phone,
                companyId = companyId,
                role = UserRole.USER,
                isApproved = false, // Requires admin approval
                tokenVersion = 1L,
                updatedAt = System.currentTimeMillis().toString(),
                createdAt = System.currentTimeMillis().toString()
            )

            firestore.collection("users").document(uid).set(profile).await()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        prefs.userId = null
        prefs.selectedCompanyId = null
    }

    override suspend fun getCurrentUserProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val profile = doc.toObject(UserProfile::class.java)
            val updatedProfile = profile?.copy(
                isApproved = doc.getBoolean("isApproved") ?: profile.isApproved,
                isActive = doc.getBoolean("isActive") ?: profile.isActive
            )
            if (updatedProfile != null && updatedProfile.tokenVersion > prefs.tokenVersion) {
                auth.signOut()
                prefs.userId = null
                prefs.selectedCompanyId = null
                return null
            }
            updatedProfile
        } catch (e: Exception) {
            null
        }
    }

    override val currentUserProfileFlow: Flow<UserProfile?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }

        val listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                val profile = snapshot?.toObject(UserProfile::class.java)
                val updatedProfile = profile?.copy(
                    isApproved = snapshot.getBoolean("isApproved") ?: profile.isApproved,
                    isActive = snapshot.getBoolean("isActive") ?: profile.isActive
                )
                if (updatedProfile != null && updatedProfile.tokenVersion > prefs.tokenVersion) {
                    auth.signOut()
                    prefs.userId = null
                    prefs.selectedCompanyId = null
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(updatedProfile)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun changeOwnPassword(currentPass: String, newPass: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("User not authenticated"))
            val email = user.email ?: return Result.failure(Exception("User email not found"))

            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPass)
            user.reauthenticate(credential).await()
            user.updatePassword(newPass).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
