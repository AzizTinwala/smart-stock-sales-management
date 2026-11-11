package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.viewmodel.DrawerViewModel
import com.saifee.inventorysalesmanagement.mvvm.presentation.navigation.AppDestinations
import kotlinx.coroutines.launch

/**
 * Enterprise StockPilot Navigation Drawer
 */
@Composable
fun AppDrawer(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: DrawerViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = AppTokens.Spacing.Medium)
        ) {
            // Brand Title & Profile Header
            DrawerHeader(
                userName = uiState.currentUserProfile?.name?.ifEmpty { "Enterprise User" } ?: "Enterprise User",
                roleName = uiState.currentUserProfile?.role?.name ?: "Warehouse Admin"
            )

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            // Company and Location Selection Section
            Column(modifier = Modifier.padding(horizontal = AppTokens.Spacing.Medium)) {
                SelectionDropdown(
                    label = "Company",
                    icon = Icons.Default.Business,
                    selectedName = uiState.companies.find { it.companyId == uiState.selectedCompanyId }?.companyName ?: "",
                    options = uiState.companies.map { it.companyId to it.companyName },
                    onSelect = { viewModel.selectCompany(it) },
                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppDestinations.SETTINGS.route)
                    }
                )

                Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

                SelectionDropdown(
                    label = "Warehouse / Location",
                    icon = Icons.Default.Warehouse,
                    selectedName = uiState.locations.find { it.locationId == uiState.selectedLocationId }?.locationName ?: "",
                    options = uiState.locations.map { it.locationId to it.locationName },
                    onSelect = { viewModel.selectLocation(it) },
                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppDestinations.SETTINGS.route)
                    },
                    enabled = uiState.selectedCompanyId != null
                )
            }

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = AppTokens.Spacing.Medium)
            )
            Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

            // Navigation Sections
            DrawerSectionHeader("MAIN MODULES")
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.DASHBOARD)
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.ITEMS)
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.ITEM_GROUP)

            DrawerSectionHeader("INVENTORY MOVEMENTS")
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.PURCHASE, customLabel = "Stock In")
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.SALES, customLabel = "Stock Out")
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.INVENTORY, customLabel = "Stock History")

            DrawerSectionHeader("PARTNERS & ENTITIES")
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.VENDORS, customLabel = "Suppliers")
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.CUSTOMERS, customLabel = "Customers")

            DrawerSectionHeader("ADMINISTRATION")
            if (uiState.canManageUsers) {
                renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.USER_MANAGEMENT)
            }
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.REPORTS)
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.SYNC)
            renderDrawerDestination(navController, drawerState, scope, currentRoute, AppDestinations.SETTINGS)

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Large))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = AppTokens.Spacing.Medium)
            )
            Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

            AppDrawerItem(
                title = "Logout",
                icon = Icons.AutoMirrored.Filled.Logout,
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(AppDestinations.LOGIN.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun renderDrawerDestination(
    navController: NavController,
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    currentRoute: String?,
    destination: AppDestinations,
    customLabel: String? = null
) {
    val isSelected = currentRoute == destination.route
    AppDrawerItem(
        title = customLabel ?: destination.label,
        icon = destination.icon,
        selected = isSelected,
        onClick = {
            scope.launch { drawerState.close() }
            if (currentRoute != destination.route) {
                navController.navigate(destination.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        }
    )
}

@Composable
private fun DrawerHeader(userName: String, roleName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = AppTokens.Dimensions.BorderWidth,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = userName,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            StatusBadge(
                label = roleName,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(
            start = AppTokens.Spacing.Medium + 12.dp,
            top = 12.dp,
            bottom = 4.dp
        )
    )
}

@Composable
private fun SelectionDropdown(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedName: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    onSettingsClick: () -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(AppTokens.CornerRadius.Input)
                )
                .border(
                    width = AppTokens.Dimensions.BorderWidth,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(AppTokens.CornerRadius.Input)
                )
                .clickable(enabled = enabled) { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.weight(1f)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onSelect(id)
                            expanded = false
                        }
                    )
                }
            }
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Edit $label",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
