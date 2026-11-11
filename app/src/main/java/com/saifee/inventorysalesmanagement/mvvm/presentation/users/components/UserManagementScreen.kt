package com.saifee.inventorysalesmanagement.mvvm.presentation.users.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saifee.inventorysalesmanagement.core.ui.components.AppEmptyState
import com.saifee.inventorysalesmanagement.core.ui.components.AppSearchField
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.presentation.common.components.ManagerPinDialog
import com.saifee.inventorysalesmanagement.mvvm.presentation.users.viewmodel.UserManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onMenuClick: () -> Unit,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSetManagerPinDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
        uiState.successMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { AppText("User Management", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    val role = uiState.currentUserProfile?.role
                    if (role == UserRole.SUPER_USER || role == UserRole.ADMIN) {
                        TextButton(onClick = { isSetManagerPinDialogOpen = true }) {
                            AppText("Set Manager PIN")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            val actorRole = uiState.currentUserProfile?.role
            if (actorRole == UserRole.SUPER_USER || actorRole == UserRole.ADMIN) {
                FloatingActionButton(onClick = { viewModel.openAddUserDialog() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add User")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                AppSearchField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = "Search users by name or email...",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Role Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = uiState.roleFilter == null,
                            onClick = { viewModel.onRoleFilterChanged(null) },
                            label = { AppText("All Roles") }
                        )
                    }
                    items(UserRole.entries.toTypedArray()) { role ->
                        FilterChip(
                            selected = uiState.roleFilter == role,
                            onClick = { viewModel.onRoleFilterChanged(role) },
                            label = { AppText(role.name) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Activity Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val activeOptions = listOf(
                        null to "All Activity",
                        true to "Active",
                        false to "Inactive"
                    )
                    items(activeOptions) { (key, label) ->
                        FilterChip(
                            selected = uiState.activeFilter == key,
                            onClick = { viewModel.onActiveFilterChanged(key) },
                            label = { AppText(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Approval Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val approvedOptions = listOf(
                        null to "All Approval",
                        true to "Approved",
                        false to "Pending"
                    )
                    items(approvedOptions) { (key, label) ->
                        FilterChip(
                            selected = uiState.approvedFilter == key,
                            onClick = { viewModel.onApprovedFilterChanged(key) },
                            label = { AppText(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.filteredUsers.isEmpty()) {
                    AppEmptyState(
                        icon = Icons.Default.Person,
                        title = "No Users Found",
                        description = "No user accounts match the current filter criteria."
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.filteredUsers, key = { it.userId }) { user ->
                            UserCardItem(
                                user = user,
                                currentUser = uiState.currentUserProfile,
                                onRoleChange = { newRole -> viewModel.updateUserRole(user, newRole) },
                                onToggleStatus = { viewModel.toggleUserActiveStatus(user) },
                                onApprove = { viewModel.approveUser(user) },
                                onForceLogout = { viewModel.forceLogoutUser(user) },
                                onOpenPermissions = { viewModel.openFeaturePermissionsDialog(user) },
                                onChangePassword = { viewModel.openChangePasswordDialog(user) }
                            )
                        }
                    }
                }
            }

            if (uiState.isAddUserDialogOpen) {
                AddEditUserDialog(
                    currentUserRole = uiState.currentUserProfile?.role ?: UserRole.USER,
                    editingUser = uiState.editingUser,
                    companies = uiState.companies,
                    selectedCompanyId = uiState.selectedCompanyId,
                    onDismiss = { viewModel.closeAddUserDialog() },
                    onSubmit = { name, email, pass, companyId, role ->
                        viewModel.createUser(name, email, pass, companyId, role)
                    }
                )
            }

            if (uiState.isFeaturePermissionsDialogOpen && uiState.permissionsTargetUser != null) {
                FeaturePermissionsDialog(
                    user = uiState.permissionsTargetUser!!,
                    onDismiss = { viewModel.closeFeaturePermissionsDialog() },
                    onSubmit = { newPermissions ->
                        viewModel.updateUserFeaturePermissions(uiState.permissionsTargetUser!!, newPermissions)
                    }
                )
            }

            if (uiState.isChangePasswordDialogOpen && uiState.changePasswordTargetUser != null) {
                ChangeUserPasswordDialog(
                    user = uiState.changePasswordTargetUser!!,
                    onDismiss = { viewModel.closeChangePasswordDialog() },
                    onConfirmPasswordChange = { newPass ->
                        viewModel.changeUserPassword(uiState.changePasswordTargetUser!!, newPass)
                    },
                    onSendResetEmail = {
                        viewModel.sendResetPasswordEmail(uiState.changePasswordTargetUser!!)
                    }
                )
            }

            if (isSetManagerPinDialogOpen) {
                ManagerPinDialog(
                    title = "Set Manager Override PIN",
                    message = "Enter a 4-to-6 digit PIN used by managers to authorize restricted actions (discounts > 10%, manual stock adjustments, voids).",
                    onConfirmPin = { pin ->
                        viewModel.setManagerPin(pin)
                        isSetManagerPinDialogOpen = false
                    },
                    onDismiss = { isSetManagerPinDialogOpen = false }
                )
            }
        }
    }
}

@Composable
fun UserCardItem(
    user: UserProfile,
    currentUser: UserProfile?,
    onRoleChange: (UserRole) -> Unit,
    onToggleStatus: () -> Unit,
    onApprove: () -> Unit,
    onForceLogout: () -> Unit,
    onOpenPermissions: () -> Unit,
    onChangePassword: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val canModifyUser = currentUser != null &&
            currentUser.userId != user.userId &&
            user.role != UserRole.SUPER_USER &&
            (currentUser.role == UserRole.SUPER_USER || (currentUser.role == UserRole.ADMIN && currentUser.companyId == user.companyId))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                AppText(
                    text = user.name.ifEmpty { "User" },
                    style = MaterialTheme.typography.titleMedium
                )
                AppText(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (user.phone.isNotEmpty()) {
                    AppText(
                        text = "Phone: ${user.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Role Badge
                    UserBadge(
                        text = user.role.name,
                        backgroundColor = when (user.role) {
                            UserRole.SUPER_USER -> MaterialTheme.colorScheme.errorContainer
                            UserRole.ADMIN -> MaterialTheme.colorScheme.primaryContainer
                            UserRole.USER -> MaterialTheme.colorScheme.secondaryContainer
                            UserRole.CASHIER -> MaterialTheme.colorScheme.tertiaryContainer
                            UserRole.VIEWER -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Approval Badge
                    UserBadge(
                        text = if (user.isApproved) "APPROVED" else "PENDING",
                        backgroundColor = if (user.isApproved) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        textColor = if (user.isApproved) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Active Badge
                    UserBadge(
                        text = if (user.isActive) "ACTIVE" else "INACTIVE",
                        backgroundColor = if (user.isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        textColor = if (user.isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }

            if (!user.isApproved && canModifyUser) {
                TextButton(onClick = onApprove) {
                    AppText("Approve", color = MaterialTheme.colorScheme.primary)
                }
            }

            if (canModifyUser) {
                Box {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "User Actions")
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        if (!user.isApproved) {
                            DropdownMenuItem(
                                text = { AppText("Approve User") },
                                onClick = {
                                    isMenuExpanded = false
                                    onApprove()
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { AppText("Feature Permissions") },
                            onClick = {
                                isMenuExpanded = false
                                onOpenPermissions()
                            }
                        )

                        DropdownMenuItem(
                            text = { AppText("Change Password") },
                            onClick = {
                                isMenuExpanded = false
                                onChangePassword()
                            }
                        )

                        DropdownMenuItem(
                            text = { AppText(if (user.isActive) "Deactivate User" else "Activate User") },
                            onClick = {
                                isMenuExpanded = false
                                onToggleStatus()
                            }
                        )

                        DropdownMenuItem(
                            text = { AppText("Force Logout Session") },
                            onClick = {
                                isMenuExpanded = false
                                onForceLogout()
                            }
                        )

                        DropdownMenuItem(
                            text = { AppText("Set Role: ADMIN") },
                            onClick = {
                                isMenuExpanded = false
                                onRoleChange(UserRole.ADMIN)
                            }
                        )

                        DropdownMenuItem(
                            text = { AppText("Set Role: USER") },
                            onClick = {
                                isMenuExpanded = false
                                onRoleChange(UserRole.USER)
                            }
                        )

                        DropdownMenuItem(
                            text = { AppText("Set Role: CASHIER") },
                            onClick = {
                                isMenuExpanded = false
                                onRoleChange(UserRole.CASHIER)
                            }
                        )

                        DropdownMenuItem(
                            text = { AppText("Set Role: VIEWER") },
                            onClick = {
                                isMenuExpanded = false
                                onRoleChange(UserRole.VIEWER)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        AppText(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
