package com.saifee.inventorysalesmanagement.mvvm.domain.repository

import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<UserProfile>
    suspend fun register(email: String, pass: String, name: String, companyId: String, phone: String): Result<UserProfile>
    suspend fun logout()
    suspend fun getCurrentUserProfile(): UserProfile?
    val currentUserProfileFlow: Flow<UserProfile?>
    suspend fun changeOwnPassword(currentPass: String, newPass: String): Result<Unit>
}
