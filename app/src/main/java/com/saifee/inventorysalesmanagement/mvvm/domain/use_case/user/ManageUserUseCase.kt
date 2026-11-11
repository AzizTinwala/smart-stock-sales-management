package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.UserManagementRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

sealed class UserManagementAction {
    data class CreateUser(val profile: UserProfile, val password: String) : UserManagementAction()
    data class UpdateUser(val profile: UserProfile) : UserManagementAction()
    data class SetActiveStatus(val targetUser: UserProfile, val isActive: Boolean) : UserManagementAction()
    data class ApproveUser(val targetUser: UserProfile) : UserManagementAction()
    data class ForceLogout(val targetUser: UserProfile) : UserManagementAction()
    data class ChangeRole(val targetUser: UserProfile, val newRole: UserRole) : UserManagementAction()
    data class UpdateFeaturePermissions(val targetUser: UserProfile, val featurePermissions: Map<String, Boolean>) : UserManagementAction()
    data class ChangePassword(val targetUser: UserProfile, val newPassword: String) : UserManagementAction()
    data class SendResetPasswordEmail(val targetUser: UserProfile) : UserManagementAction()
}

class ManageUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userManagementRepository: UserManagementRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<UserManagementAction, Result<Unit>>(dispatcher) {

    override suspend fun execute(params: UserManagementAction): Result<Unit> {
        val actor = authRepository.getCurrentUserProfile()
            ?: return Result.failure(SecurityException("User is unauthenticated."))

        return when (params) {
            is UserManagementAction.CreateUser -> {
                if (!authorizationManager.canCreateUser(actor, params.profile.companyId)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot create user in target company."))
                }
                userManagementRepository.createUser(params.profile, params.password).map { }
            }
            is UserManagementAction.UpdateUser -> {
                if (!authorizationManager.canManageUsers(actor, params.profile.companyId)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot update user in target company."))
                }
                userManagementRepository.updateUserProfile(params.profile)
            }
            is UserManagementAction.SetActiveStatus -> {
                if (!authorizationManager.canDeactivateUser(actor, params.targetUser)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot modify activation status for user."))
                }
                userManagementRepository.setUserActiveStatus(params.targetUser.userId, params.isActive)
            }
            is UserManagementAction.ApproveUser -> {
                if (!authorizationManager.canManageUsers(actor, params.targetUser.companyId)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot approve user."))
                }
                userManagementRepository.approveUser(params.targetUser.userId)
            }
            is UserManagementAction.ForceLogout -> {
                if (!authorizationManager.canManageUsers(actor, params.targetUser.companyId)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot force logout user."))
                }
                userManagementRepository.forceLogoutUser(params.targetUser.userId)
            }
            is UserManagementAction.ChangeRole -> {
                if (!authorizationManager.canChangeUserRole(actor, params.targetUser, params.newRole)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot change user role to ${params.newRole}."))
                }
                userManagementRepository.updateUserRole(params.targetUser.userId, params.newRole)
            }
            is UserManagementAction.UpdateFeaturePermissions -> {
                if (!authorizationManager.canManageUsers(actor, params.targetUser.companyId)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot update feature permissions for user."))
                }
                userManagementRepository.updateUserFeaturePermissions(params.targetUser.userId, params.featurePermissions)
            }
            is UserManagementAction.ChangePassword -> {
                if (!authorizationManager.canManageUsers(actor, params.targetUser.companyId)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot change user password."))
                }
                userManagementRepository.changeUserPassword(params.targetUser.userId, params.newPassword)
            }
            is UserManagementAction.SendResetPasswordEmail -> {
                if (!authorizationManager.canManageUsers(actor, params.targetUser.companyId)) {
                    return Result.failure(SecurityException("Unauthorized: Cannot send reset email."))
                }
                userManagementRepository.sendPasswordResetEmail(params.targetUser.email)
            }
        }
    }
}
