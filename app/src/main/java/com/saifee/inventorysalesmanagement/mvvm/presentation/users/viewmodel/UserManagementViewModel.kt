package com.saifee.inventorysalesmanagement.mvvm.presentation.users.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.UserManagementRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user.GetUsersParams
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user.GetUsersUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user.ManageUserUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user.UserManagementAction
import com.saifee.inventorysalesmanagement.mvvm.presentation.users.state.UserManagementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val manageUserUseCase: ManageUserUseCase,
    private val authRepository: AuthRepository,
    private val tenantRepository: TenantRepository,
    private val userManagementRepository: UserManagementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = authRepository.getCurrentUserProfile()
            _uiState.update { it.copy(currentUserProfile = currentUser) }

            tenantRepository.getCompanies().collectLatest { companies ->
                _uiState.update { it.copy(companies = companies) }
            }
        }

        viewModelScope.launch {
            combine(
                tenantRepository.selectedCompanyIdFlow,
                authRepository.currentUserProfileFlow
            ) { selectedCompanyId, currentUser ->
                val targetCompany = if (currentUser?.role == UserRole.SUPER_USER) {
                    selectedCompanyId ?: ""
                } else {
                    currentUser?.companyId ?: ""
                }
                _uiState.update { it.copy(selectedCompanyId = targetCompany, currentUserProfile = currentUser) }
                targetCompany
            }.collectLatest { companyId ->
                when (val result = getUsersUseCase(GetUsersParams(companyId))) {
                    is ResultState.Success -> {
                        result.data.collectLatest { users ->
                            _uiState.update { currentState ->
                                currentState.copy(
                                    users = users,
                                    isLoading = false,
                                    filteredUsers = filterUsersList(
                                        users = users,
                                        query = currentState.searchQuery,
                                        roleFilter = currentState.roleFilter,
                                        activeFilter = currentState.activeFilter,
                                        approvedFilter = currentState.approvedFilter
                                    )
                                )
                            }
                        }
                    }
                    is ResultState.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                    else -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredUsers = filterUsersList(
                    users = currentState.users,
                    query = query,
                    roleFilter = currentState.roleFilter,
                    activeFilter = currentState.activeFilter,
                    approvedFilter = currentState.approvedFilter
                )
            )
        }
    }

    fun onRoleFilterChanged(role: UserRole?) {
        _uiState.update { currentState ->
            currentState.copy(
                roleFilter = role,
                filteredUsers = filterUsersList(
                    users = currentState.users,
                    query = currentState.searchQuery,
                    roleFilter = role,
                    activeFilter = currentState.activeFilter,
                    approvedFilter = currentState.approvedFilter
                )
            )
        }
    }

    fun onActiveFilterChanged(active: Boolean?) {
        _uiState.update { currentState ->
            currentState.copy(
                activeFilter = active,
                filteredUsers = filterUsersList(
                    users = currentState.users,
                    query = currentState.searchQuery,
                    roleFilter = currentState.roleFilter,
                    activeFilter = active,
                    approvedFilter = currentState.approvedFilter
                )
            )
        }
    }

    fun onApprovedFilterChanged(approved: Boolean?) {
        _uiState.update { currentState ->
            currentState.copy(
                approvedFilter = approved,
                filteredUsers = filterUsersList(
                    users = currentState.users,
                    query = currentState.searchQuery,
                    roleFilter = currentState.roleFilter,
                    activeFilter = currentState.activeFilter,
                    approvedFilter = approved
                )
            )
        }
    }

    fun openAddUserDialog(userToEdit: UserProfile? = null) {
        _uiState.update {
            it.copy(
                isAddUserDialogOpen = true,
                editingUser = userToEdit,
                errorMessage = null
            )
        }
    }

    fun closeAddUserDialog() {
        _uiState.update {
            it.copy(
                isAddUserDialogOpen = false,
                editingUser = null,
                errorMessage = null
            )
        }
    }

    fun createUser(name: String, email: String, pass: String, companyId: String, role: UserRole) {
        viewModelScope.launch {
            val profile = UserProfile(
                name = name,
                email = email,
                companyId = companyId.ifEmpty { uiState.value.selectedCompanyId ?: "" },
                role = role,
                isApproved = true,
                isActive = true
            )

            when (val res = manageUserUseCase(UserManagementAction.CreateUser(profile, pass))) {
                is ResultState.Success -> {
                    res.data.onSuccess {
                        closeAddUserDialog()
                    }.onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to create user.") }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(errorMessage = res.message) }
                }
                ResultState.Loading -> {}
            }
        }
    }

    fun updateUserRole(targetUser: UserProfile, newRole: UserRole) {
        viewModelScope.launch {
            when (val res = manageUserUseCase(UserManagementAction.ChangeRole(targetUser, newRole))) {
                is ResultState.Success -> {
                    res.data.onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to update user role.") }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(errorMessage = res.message) }
                }
                ResultState.Loading -> {}
            }
        }
    }

    fun toggleUserActiveStatus(targetUser: UserProfile) {
        viewModelScope.launch {
            val newActive = !targetUser.isActive
            when (val res = manageUserUseCase(UserManagementAction.SetActiveStatus(targetUser, newActive))) {
                is ResultState.Success -> {
                    res.data.onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to update user status.") }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(errorMessage = res.message) }
                }
                ResultState.Loading -> {}
            }
        }
    }

    fun approveUser(targetUser: UserProfile) {
        viewModelScope.launch {
            when (val res = manageUserUseCase(UserManagementAction.ApproveUser(targetUser))) {
                is ResultState.Success -> {
                    res.data.onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to approve user.") }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(errorMessage = res.message) }
                }
                ResultState.Loading -> {}
            }
        }
    }

    fun forceLogoutUser(targetUser: UserProfile) {
        viewModelScope.launch {
            when (val res = manageUserUseCase(UserManagementAction.ForceLogout(targetUser))) {
                is ResultState.Success -> {
                    res.data.onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to force logout user.") }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(errorMessage = res.message) }
                }
                ResultState.Loading -> {}
            }
        }
    }

    fun openFeaturePermissionsDialog(targetUser: UserProfile) {
        _uiState.update {
            it.copy(
                isFeaturePermissionsDialogOpen = true,
                permissionsTargetUser = targetUser,
                errorMessage = null
            )
        }
    }

    fun closeFeaturePermissionsDialog() {
        _uiState.update {
            it.copy(
                isFeaturePermissionsDialogOpen = false,
                permissionsTargetUser = null,
                errorMessage = null
            )
        }
    }

    fun updateUserFeaturePermissions(targetUser: UserProfile, permissions: Map<String, Boolean>) {
        viewModelScope.launch {
            when (val res = manageUserUseCase(UserManagementAction.UpdateFeaturePermissions(targetUser, permissions))) {
                is ResultState.Success -> {
                    res.data.onSuccess {
                        closeFeaturePermissionsDialog()
                    }.onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to update permissions.") }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(errorMessage = res.message) }
                }
                ResultState.Loading -> {}
            }
        }
    }

    fun openChangePasswordDialog(targetUser: UserProfile) {
        _uiState.update {
            it.copy(
                isChangePasswordDialogOpen = true,
                changePasswordTargetUser = targetUser,
                errorMessage = null
            )
        }
    }

    fun closeChangePasswordDialog() {
        _uiState.update {
            it.copy(
                isChangePasswordDialogOpen = false,
                changePasswordTargetUser = null,
                errorMessage = null
            )
        }
    }

    fun changeUserPassword(targetUser: UserProfile, newPass: String) {
        viewModelScope.launch {
            when (val res = manageUserUseCase(UserManagementAction.ChangePassword(targetUser, newPass))) {
                is ResultState.Success -> {
                    res.data.onSuccess {
                        closeChangePasswordDialog()
                        _uiState.update { it.copy(successMessage = "Password updated successfully for ${targetUser.name.ifEmpty { targetUser.email }}") }
                    }.onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to change password.") }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(errorMessage = res.message) }
                }
                ResultState.Loading -> {}
            }
        }
    }

    fun sendResetPasswordEmail(targetUser: UserProfile) {
        viewModelScope.launch {
            when (val res = manageUserUseCase(UserManagementAction.SendResetPasswordEmail(targetUser))) {
                is ResultState.Success -> {
                    res.data.onSuccess {
                        _uiState.update { it.copy(successMessage = "Password reset email sent to ${targetUser.email}") }
                    }.onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to send reset email.") }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(errorMessage = res.message) }
                }
                ResultState.Loading -> {}
            }
        }
    }

    fun setManagerPin(pin: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.currentUserProfile
            if (currentUser != null && (currentUser.role == UserRole.ADMIN || currentUser.role == UserRole.SUPER_USER)) {
                userManagementRepository.setManagerPin(currentUser.userId, pin)
                    .onSuccess {
                        _uiState.update { it.copy(successMessage = "Manager Override PIN updated successfully.") }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.message ?: "Failed to set Manager PIN.") }
                    }
            } else {
                _uiState.update { it.copy(errorMessage = "Unauthorized: Only Admins can set the Manager PIN.") }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun filterUsersList(
        users: List<UserProfile>,
        query: String,
        roleFilter: UserRole?,
        activeFilter: Boolean?,
        approvedFilter: Boolean?
    ): List<UserProfile> {
        return users.filter { user ->
            val matchesQuery = query.isEmpty() ||
                    user.name.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true)

            val matchesRole = roleFilter == null || user.role == roleFilter

            val matchesActive = activeFilter == null || user.isActive == activeFilter

            val matchesApproved = approvedFilter == null || user.isApproved == approvedFilter

            matchesQuery && matchesRole && matchesActive && matchesApproved
        }
    }
}
