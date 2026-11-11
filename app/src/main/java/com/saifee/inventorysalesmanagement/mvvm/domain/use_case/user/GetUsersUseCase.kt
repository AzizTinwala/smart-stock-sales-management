package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.UserManagementRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

data class GetUsersParams(
    val companyId: String? = null
)

class GetUsersUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userManagementRepository: UserManagementRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<GetUsersParams, Flow<List<UserProfile>>>(dispatcher) {

    override suspend fun execute(params: GetUsersParams): Flow<List<UserProfile>> {
        val currentActor = authRepository.getCurrentUserProfile() ?: return flowOf(emptyList())
        val targetCompany = params.companyId ?: currentActor.companyId

        if (!authorizationManager.canViewData(currentActor, targetCompany)) {
            return flowOf(emptyList())
        }

        return if (currentActor.role == UserRole.SUPER_USER && (params.companyId == null || params.companyId.isEmpty())) {
            userManagementRepository.getAllUsers()
        } else {
            userManagementRepository.getUsersByCompany(targetCompany)
        }
    }
}
