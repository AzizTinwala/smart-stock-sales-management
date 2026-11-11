package com.saifee.inventorysalesmanagement.mvvm.presentation.users.state

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole

data class UserManagementUiState(
    val users: List<UserProfile> = emptyList(),
    val filteredUsers: List<UserProfile> = emptyList(),
    val companies: List<CompanyEntity> = emptyList(),
    val selectedCompanyId: String? = null,
    val searchQuery: String = "",
    val roleFilter: UserRole? = null,
    val activeFilter: Boolean? = null, // null = All, true = Active, false = Inactive
    val approvedFilter: Boolean? = null, // null = All, true = Approved, false = Unapproved
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUserProfile: UserProfile? = null,
    val isAddUserDialogOpen: Boolean = false,
    val editingUser: UserProfile? = null,
    val isFeaturePermissionsDialogOpen: Boolean = false,
    val permissionsTargetUser: UserProfile? = null,
    val isChangePasswordDialogOpen: Boolean = false,
    val changePasswordTargetUser: UserProfile? = null,
    val successMessage: String? = null
)
