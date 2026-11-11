package com.saifee.inventorysalesmanagement.mvvm.presentation.item.state

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity

data class ItemGroupUiState(
    val search: String = "",
    val isLoading: Boolean = false,
    val itemGroups: List<ItemGroupEntity> = emptyList(),
    
    // Add/Edit Group fields
    val showAddEditDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val editingGroupId: Long? = null,
    val groupName: String = "",
    val groupDescription: String = "",
    val groupNameError: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
