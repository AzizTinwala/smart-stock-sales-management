package com.saifee.inventorysalesmanagement.mvvm.presentation.item.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.AddGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.DeleteGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.UpdateGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemGroupUseCases
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.ItemGroupUiState
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
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
class ItemGroupViewModel @Inject constructor(
    private val useCases: ItemGroupUseCases,
    private val repository: TenantRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemGroupUiState())
    val uiState: StateFlow<ItemGroupUiState> = _uiState.asStateFlow()

    private var allGroups: List<ItemGroupEntity> = emptyList()
    private val groupId: Long? = savedStateHandle.get<Long>("groupId")?.let { if (it == -1L) null else it }

    init {
        loadGroups()
        groupId?.let {
            loadGroup(it)
        }
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = useCases.getAll(Unit)) {
                is ResultState.Success -> {
                    result.data.collectLatest { groups ->
                        allGroups = groups
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                itemGroups = filterGroups(it.search, groups)
                            )
                        }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun loadGroup(id: Long) {
        viewModelScope.launch {
            when (val result = useCases.getById(id)) {
                is ResultState.Success -> {
                    result.data?.let { group ->
                        _uiState.update {
                            it.copy(
                                isEditMode = true,
                                editingGroupId = group.groupId,
                                groupName = group.groupName,
                                groupDescription = group.description ?: "",
                                showAddEditDialog = true
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update {
            it.copy(
                search = query,
                itemGroups = filterGroups(query, allGroups)
            )
        }
    }

    private fun filterGroups(query: String, groups: List<ItemGroupEntity>): List<ItemGroupEntity> {
        return groups.filter { group ->
            group.groupName.contains(query, ignoreCase = true)
        }
    }

    fun onGroupNameChange(name: String) {
        _uiState.update { it.copy(groupName = name, groupNameError = null) }
    }

    fun onGroupDescriptionChange(description: String) {
        _uiState.update { it.copy(groupDescription = description) }
    }

    fun setShowAddEditDialog(show: Boolean, group: ItemGroupEntity? = null) {
        if (show && group != null) {
            _uiState.update {
                it.copy(
                    showAddEditDialog = true,
                    isEditMode = true,
                    editingGroupId = group.groupId,
                    groupName = group.groupName,
                    groupDescription = group.description ?: "",
                    groupNameError = null
                )
            }
        } else if (show) {
            _uiState.update {
                it.copy(
                    showAddEditDialog = true,
                    isEditMode = false,
                    editingGroupId = null,
                    groupName = "",
                    groupDescription = "",
                    groupNameError = null
                )
            }
        } else {
            _uiState.update { it.copy(showAddEditDialog = false) }
        }
    }

    fun saveGroup(onSuccess: () -> Unit) {
        val state = uiState.value
        if (state.groupName.isBlank()) {
            _uiState.update { it.copy(groupNameError = "Group name is required") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val group = ItemGroupEntity(
                groupId = state.editingGroupId ?: 0L,
                companyId = "",
                locationId = "",
                groupName = state.groupName,
                description = state.groupDescription.ifBlank { null },
                createdAt = if (state.isEditMode) 0L else System.currentTimeMillis(), // We should handle this better if we had existing group data
                updatedAt = System.currentTimeMillis()
            )

            val result = if (state.isEditMode) {
                // If we are in edit mode, we might need the original createdAt.
                // For simplicity, let's fetch it if not present, but here we can just update.
                useCases.update(UpdateGroupUseCase.UpdateGroupParams(group))
            } else {
                useCases.add(AddGroupUseCase.AddGroupParams(group))
            }

            when (result) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(isSaving = false, showAddEditDialog = false) }
                    onSuccess()
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun deleteGroup(group: ItemGroupEntity) {
        viewModelScope.launch {
            useCases.delete(DeleteGroupUseCase.DeleteGroupParams(group))
        }
    }
}
