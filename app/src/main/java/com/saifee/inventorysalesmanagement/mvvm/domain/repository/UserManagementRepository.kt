package com.saifee.inventorysalesmanagement.mvvm.domain.repository

import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface UserManagementRepository {
    fun getUsersByCompany(companyId: String): Flow<List<UserProfile>>
    fun getAllUsers(): Flow<List<UserProfile>>
    suspend fun createUser(profile: UserProfile, password: String): Result<UserProfile>
    suspend fun updateUserProfile(profile: UserProfile): Result<Unit>
    suspend fun setUserActiveStatus(userId: String, isActive: Boolean): Result<Unit>
    suspend fun approveUser(userId: String): Result<Unit>
    suspend fun forceLogoutUser(userId: String): Result<Unit>
    suspend fun updateUserRole(userId: String, newRole: UserRole): Result<Unit>
    suspend fun updateUserFeaturePermissions(userId: String, featurePermissions: Map<String, Boolean>): Result<Unit>
    suspend fun changeUserPassword(userId: String, newPassword: String): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun setManagerPin(userId: String, pin: String): Result<Unit>
    suspend fun verifyManagerPin(companyId: String, pin: String): Result<Boolean>
}
