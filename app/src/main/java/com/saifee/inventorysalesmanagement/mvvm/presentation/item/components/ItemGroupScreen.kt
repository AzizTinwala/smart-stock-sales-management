package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.enums.AppTextStyle
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppEmptyState
import com.saifee.inventorysalesmanagement.core.ui.components.AppFloatingActionButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppSearchField
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.presentation.common.components.DeleteConfirmationDialog
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.viewmodel.ItemGroupViewModel
import androidx.compose.material.icons.outlined.Category

import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.ItemGroupUiState
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemGroupScreen(
    viewModel: ItemGroupViewModel = hiltViewModel(),
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGroupClick: (ItemGroupEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ItemGroupScreenContent(
        uiState = uiState,
        onMenuClick = onMenuClick,
        onProfileClick = onProfileClick,
        onGroupClick = onGroupClick,
        onSearchChange = viewModel::onSearchChange,
        onAddGroupClick = { viewModel.setShowAddEditDialog(true) },
        onEditGroupClick = { viewModel.setShowAddEditDialog(true, it) },
        onDeleteGroupClick = { viewModel.deleteGroup(it) },
        onGroupNameChange = viewModel::onGroupNameChange,
        onGroupDescriptionChange = viewModel::onGroupDescriptionChange,
        onDismissDialog = { viewModel.setShowAddEditDialog(false) },
        onSaveGroup = { viewModel.saveGroup { viewModel.setShowAddEditDialog(false) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemGroupScreenContent(
    uiState: ItemGroupUiState,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onGroupClick: (ItemGroupEntity) -> Unit,
    onSearchChange: (String) -> Unit,
    onAddGroupClick: () -> Unit,
    onEditGroupClick: (ItemGroupEntity) -> Unit,
    onDeleteGroupClick: (ItemGroupEntity) -> Unit,
    onGroupNameChange: (String) -> Unit,
    onGroupDescriptionChange: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onSaveGroup: () -> Unit
) {
    var groupToDelete by remember { mutableStateOf<ItemGroupEntity?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Item Groups",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick,
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(onClick = onAddGroupClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Group")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small)
        ) {
            // Search Bar
            AppSearchField(
                value = uiState.search,
                onValueChange = onSearchChange,
                placeholder = "Search groups...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.itemGroups.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Outlined.Category,
                    title = if (uiState.search.isEmpty()) "No Groups" else "No matching groups",
                    description = if (uiState.search.isEmpty()) "Organize your items by creating groups" else "Try a different search term",
                    actionText = if (uiState.search.isEmpty()) "Add Group" else "Clear Search",
                    onActionClick = { 
                        if (uiState.search.isEmpty()) onAddGroupClick()
                        else onSearchChange("")
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small)
                ) {
                    items(uiState.itemGroups) { group ->
                        GroupItemCard(
                            group = group,
                            onEditClick = { onEditGroupClick(group) },
                            onDeleteClick = { groupToDelete = group },
                            onGroupClick = { onGroupClick(group) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddEditDialog) {
        ItemGroupBottomSheet(
            isEditMode = uiState.isEditMode,
            groupName = uiState.groupName,
            onGroupNameChange = onGroupNameChange,
            groupDescription = uiState.groupDescription,
            onGroupDescriptionChange = onGroupDescriptionChange,
            groupNameError = uiState.groupNameError,
            isSaving = uiState.isSaving,
            errorMessage = uiState.errorMessage,
            onDismiss = onDismissDialog,
            onSave = onSaveGroup
        )
    }

    groupToDelete?.let { group ->
        DeleteConfirmationDialog(
            onConfirm = {
                onDeleteGroupClick(group)
                groupToDelete = null
            },
            onDismiss = { groupToDelete = null },
            message = "Are you sure you want to delete group '${group.groupName}'?"
        )
    }
}

@Composable
fun GroupItemCard(
    group: ItemGroupEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onGroupClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onGroupClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppText(
                    text = group.groupName,
                    style = AppTextStyle.TITLE,
                    fontWeight = FontWeight.Bold
                )
                group.description?.let {
                    AppText(text = it, style = AppTextStyle.LABEL, color = Color.Gray)
                }
            }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemGroupScreenPreview() {
    InventorySalesManagementTheme {
        ItemGroupScreenContent(
            uiState = ItemGroupUiState(
                itemGroups = listOf(
                    ItemGroupEntity(
                        groupId = 1L,
                        companyId = "C1",
                        locationId = "L1",
                        groupName = "Group 1",
                        description = "Sample Description",
                        createdAt = 0L,
                        updatedAt = 0L
                    ),
                    ItemGroupEntity(
                        groupId = 2L,
                        companyId = "C1",
                        locationId = "L1",
                        groupName = "Group 2",
                        description = "Another Description",
                        createdAt = 0L,
                        updatedAt = 0L
                    )
                )
            ),
            onMenuClick = {},
            onProfileClick = {},
            onGroupClick = {},
            onSearchChange = {},
            onAddGroupClick = {},
            onEditGroupClick = {},
            onDeleteGroupClick = {},
            onGroupNameChange = {},
            onGroupDescriptionChange = {},
            onDismissDialog = {},
            onSaveGroup = {}
        )
    }
}
