package com.saifee.inventorysalesmanagement.mvvm.presentation.users.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppPasswordField
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditUserDialog(
    currentUserRole: UserRole,
    editingUser: UserProfile?,
    companies: List<CompanyEntity>,
    selectedCompanyId: String?,
    onDismiss: () -> Unit,
    onSubmit: (name: String, email: String, pass: String, companyId: String, role: UserRole) -> Unit
) {
    var name by remember { mutableStateOf(editingUser?.name ?: "") }
    var email by remember { mutableStateOf(editingUser?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var selectedCompany by remember {
        mutableStateOf(editingUser?.companyId ?: selectedCompanyId ?: companies.firstOrNull()?.companyId ?: "")
    }
    var selectedRole by remember { mutableStateOf(editingUser?.role ?: UserRole.USER) }

    var isCompanyExpanded by remember { mutableStateOf(false) }
    var isRoleExpanded by remember { mutableStateOf(false) }

    val availableRoles = if (currentUserRole == UserRole.SUPER_USER) {
        listOf(UserRole.ADMIN, UserRole.USER, UserRole.CASHIER, UserRole.VIEWER) // SUPER_USER protection rule
    } else {
        listOf(UserRole.ADMIN, UserRole.USER, UserRole.CASHIER, UserRole.VIEWER)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AppText(
                text = if (editingUser == null) "Create New User" else "Edit User Details",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    enabled = editingUser == null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (editingUser == null) {
                    AppPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Company Selector
                val isSuperUser = currentUserRole == UserRole.SUPER_USER
                val selectedCompanyName = companies.find { it.companyId == selectedCompany }?.companyName
                    ?: selectedCompany.ifEmpty { "Assigned Company" }

                if (isSuperUser) {
                    ExposedDropdownMenuBox(
                        expanded = isCompanyExpanded,
                        onExpandedChange = { isCompanyExpanded = !isCompanyExpanded }
                    ) {
                        AppTextField(
                            value = selectedCompanyName,
                            onValueChange = {},
                            readOnly = true,
                            label = "Assigned Company",
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCompanyExpanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isCompanyExpanded,
                            onDismissRequest = { isCompanyExpanded = false }
                        ) {
                            companies.forEach { company ->
                                DropdownMenuItem(
                                    text = { AppText(company.companyName) },
                                    onClick = {
                                        selectedCompany = company.companyId
                                        isCompanyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    AppTextField(
                        value = selectedCompanyName,
                        onValueChange = {},
                        enabled = false,
                        readOnly = true,
                        label = "Assigned Company (Fixed for Admin)",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Role Selector
                ExposedDropdownMenuBox(
                    expanded = isRoleExpanded,
                    onExpandedChange = { isRoleExpanded = !isRoleExpanded }
                ) {
                    AppTextField(
                        value = selectedRole.name,
                        onValueChange = {},
                        readOnly = true,
                        label = "User Role",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = isRoleExpanded,
                        onDismissRequest = { isRoleExpanded = false }
                    ) {
                        availableRoles.forEach { role ->
                            DropdownMenuItem(
                                text = { AppText(role.name) },
                                onClick = {
                                    selectedRole = role
                                    isRoleExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            AppButton(
                text = if (editingUser == null) "Create User" else "Save Changes",
                enabled = name.isNotBlank() && email.isNotBlank() && (editingUser != null || password.length >= 6),
                onClick = {
                    onSubmit(name, email, password, selectedCompany, selectedRole)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AppText("Cancel")
            }
        }
    )
}
