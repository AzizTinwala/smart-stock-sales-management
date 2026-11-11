package com.saifee.inventorysalesmanagement.mvvm.presentation.item.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.core.sealed_classes.InputState
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AddItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.UpdateItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.AddGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemGroupUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemUseCases
import com.saifee.inventorysalesmanagement.mvvm.core.common_utils.LocalFileHelper
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.AddItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
    private val groupUseCases: ItemGroupUseCases,
    private val tenantRepository: TenantRepository,
    private val syncRepository: SyncRepository,
    private val localFileHelper: LocalFileHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val itemId: Long? = savedStateHandle.get<Long>("itemId")?.let { if (it == -1L) null else it }
    val isEditMode = itemId != null

    private val _addItemUiState = MutableStateFlow(
        AddItemUiState(
            unit = "Pieces",
            trackInventory = true
        )
    )
    val addItemUiState: StateFlow<AddItemUiState> = _addItemUiState.asStateFlow()

    private var allGroups: List<ItemGroupEntity> = emptyList()
    private var existingItem: ItemEntity? = null

    init {
        loadGroups()
        itemId?.let { loadItem(it) }
    }

    private fun loadItem(id: Long) {
        viewModelScope.launch {
            when (val result = itemUseCases.getById(id)) {
                is ResultState.Success -> {
                    val item = result.data
                    existingItem = item
                    item?.let {
                        updateState {
                            copy(
                                itemName = it.itemName,
                                barcode = it.barcode ?: "",
                                costPrice = it.purchasePrice.toString(),
                                sellPrice = it.sellingPrice.toString(),
                                unit = it.unit ?: "Pieces",
                                gstPercentage = it.gstPercentage.toString(),
                                hsnNumber = it.hsnNumber ?: "",
                                lowStockValue = it.lowStockLevel.toString(),
                                criticalStockValue = it.criticalStockLevel.toString(),
                                tempImagePath = it.imagePath
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun loadGroups() {
        viewModelScope.launch {
            when (val result = groupUseCases.getAll(Unit)) {
                is ResultState.Success -> {
                    result.data.collect { groups ->
                        allGroups = groups
                        val groupNames = groups.map { it.groupName }.toMutableList()
                        if (!groupNames.contains("None")) {
                            groupNames.add(0, "None")
                        }
                        updateState { copy(availableGroups = groupNames) }
                        
                        // If editing, find the group name
                        existingItem?.let { item ->
                            groups.find { it.groupId == item.groupId }?.let { group ->
                                updateState { copy(itemGroup = group.groupName) }
                            }
                        }
                    }
                }
                else -> {
                    updateState { copy(availableGroups = listOf("None")) }
                }
            }
        }
    }

    fun onItemNameChange(value: String) {
        updateState { copy(itemName = value, itemNameError = null, itemNameInputState = InputState.Normal) }
    }

    fun onItemGroupChange(value: String) {
        updateState { copy(itemGroup = value, itemGroupError = null, itemGroupInputState = InputState.Normal) }
    }

    fun onCostPriceChange(value: String) {
        updateState { copy(costPrice = value, costPriceError = null, costPriceInputState = InputState.Normal) }
    }

    fun onSellPriceChange(value: String) {
        updateState { copy(sellPrice = value) }
    }

    fun onUnitChange(value: String) {
        updateState { copy(unit = value) }
    }

    fun onBarcodeChange(value: String) {
        updateState { copy(barcode = value) }
    }

    fun onGstPercentageChange(value: String) {
        updateState { copy(gstPercentage = value) }
    }

    fun onHsnNumberChange(value: String) {
        updateState { copy(hsnNumber = value) }
    }

    fun onGenerateBarcode() {
        val timestamp = System.currentTimeMillis().toString().takeLast(10)
        val random = (10..99).random()
        val generated = "890$timestamp$random" // Mocking a standard GS1-like format
        onBarcodeChange(generated)
    }

    fun onTrackInventoryChanged(value: Boolean) {
        updateState { copy(trackInventory = value) }
    }

    fun onLowStockValueChange(value: String) {
        updateState { copy(lowStockValue = value) }
    }

    fun onCriticalStockValueChange(value: String) {
        updateState { copy(criticalStockValue = value) }
    }

    fun onImageDescriptionChange(value: String) {
        updateState { copy(imageDescription = value) }
    }

    fun onImageSelected(uri: Uri?) {
        updateState {
            copy(imageUri = uri)
        }
        if (uri != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val parent = tenantRepository.getSelectedCompanyId()
                val path = localFileHelper.saveToTemp(parent, uri)
                updateState { copy(tempImagePath = path) }
            }
        } else {
            updateState { copy(tempImagePath = null) }
        }
    }

    fun setShowAddGroupDialog(show: Boolean) {
        updateState { 
            copy(
                showAddGroupDialog = show,
                dialogGroupName = "",
                dialogGroupDescription = "",
                dialogGroupNameError = null
            ) 
        }
    }

    fun onDialogGroupNameChange(name: String) {
        updateState { copy(dialogGroupName = name, dialogGroupNameError = null) }
    }

    fun onDialogGroupDescriptionChange(description: String) {
        updateState { copy(dialogGroupDescription = description) }
    }

    fun addNewGroup() {
        val state = addItemUiState.value
        if (state.dialogGroupName.isBlank()) {
            updateState { copy(dialogGroupNameError = "Group name is required") }
            return
        }

        updateState { copy(isDialogSaving = true) }

        viewModelScope.launch {
            val newGroup = ItemGroupEntity(
                groupId = 0L,
                companyId = "",
                locationId = "",
                groupName = state.dialogGroupName,
                description = state.dialogGroupDescription.ifBlank { null },
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val result = groupUseCases.add(AddGroupUseCase.AddGroupParams(newGroup))
            if (result is ResultState.Success) {
                onItemGroupChange(state.dialogGroupName)
                updateState { copy(showAddGroupDialog = false, isDialogSaving = false) }
            } else {
                val error = (result as? ResultState.Error)?.message ?: "Failed to create group"
                updateState { copy(isDialogSaving = false, errorMessage = error) }
            }
        }
    }

    private fun updateState(
        block: AddItemUiState.() -> AddItemUiState
    ) {
        _addItemUiState.update { it.block() }
    }

    fun clearState() {
        _addItemUiState.update {
            AddItemUiState(
                unit = "Pieces",
                trackInventory = true,
                availableGroups = it.availableGroups,
                gstPercentage = "",
                hsnNumber = ""
            )
        }
    }

    fun saveItem(
        onSuccess: (String) -> Unit
    ) {
        val state = addItemUiState.value

        val itemNameError = if (state.itemName.isBlank()) "Item name is required" else null
        val itemGroupError = if (state.itemGroup == "None" || state.itemGroup.isBlank()) "Please select item group" else null
        val costPriceError = when {
            state.costPrice.isBlank() -> "Cost price is required"
            (state.costPrice.toDoubleOrNull() ?: 0.0) < 0 -> "Price cannot be negative"
            else -> null
        }
        val sellPriceError = when {
            (state.sellPrice.toDoubleOrNull() ?: 0.0) < 0 -> "Price cannot be negative"
            else -> null
        }

        if (itemNameError != null || itemGroupError != null || costPriceError != null || sellPriceError != null) {
            _addItemUiState.update {
                it.copy(
                    itemNameError = itemNameError,
                    itemNameInputState = if (itemNameError != null) InputState.Error else InputState.Normal,
                    itemGroupError = itemGroupError,
                    itemGroupInputState = if (itemGroupError != null) InputState.Error else InputState.Normal,
                    costPriceError = costPriceError,
                    costPriceInputState = if (costPriceError != null) InputState.Error else InputState.Normal,
                    errorMessage = sellPriceError
                )
            }
            return
        }

        _addItemUiState.update {
            it.copy(isSaving = true, errorMessage = null)
        }

        viewModelScope.launch {
            // 1. Ensure Group Exists
            val groupNameToUse = state.itemGroup.ifBlank { "None" }
            var selectedGroup = allGroups.find { it.groupName.equals(groupNameToUse, ignoreCase = true) }
            
            if (selectedGroup == null) {
                val newGroup = ItemGroupEntity(
                    groupId = 0L,
                    companyId = "", // Repository will fill this
                    locationId = "", // Repository will fill this
                    groupName = groupNameToUse,
                    description = if (groupNameToUse == "None") "Default group" else "Auto-created from Add Item",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    createdBy = "", // Repository will fill this
                    updatedBy = ""  // Repository will fill this
                )
                val groupResult = groupUseCases.add(AddGroupUseCase.AddGroupParams(newGroup))
                if (groupResult is ResultState.Success) {
                    val newId = groupResult.data
                    selectedGroup = newGroup.copy(groupId = newId)
                } else {
                    _addItemUiState.update { it.copy(isSaving = false, errorMessage = "Failed to create item group") }
                    return@launch
                }
            }

            // 2. Save Item
            val parent = tenantRepository.getSelectedCompanyId()
            val location = tenantRepository.getSelectedLocationId()
            val savedImagePath = state.tempImagePath?.let { localFileHelper.moveToNonSync(parent, it) }

            val item = ItemEntity(
                itemId = itemId ?: 0L,
                companyId = parent,
                locationId = location,
                itemName = state.itemName,
                groupId = selectedGroup.groupId,
                sku = state.barcode.ifBlank { null },
                barcode = state.barcode.ifBlank { null },
                purchasePrice = state.costPrice.toDoubleOrNull() ?: 0.0,
                sellingPrice = state.sellPrice.toDoubleOrNull() ?: 0.0,
                stockQty = existingItem?.stockQty ?: 0.0,
                unit = state.unit,
                gstPercentage = state.gstPercentage.toDoubleOrNull() ?: 0.0,
                hsnNumber = state.hsnNumber.ifBlank { null },
                lowStockLevel = state.lowStockValue.toDoubleOrNull() ?: 5.0,
                criticalStockLevel = state.criticalStockValue.toDoubleOrNull() ?: 2.0,
                imagePath = savedImagePath ?: existingItem?.imagePath,
                imageUrl = existingItem?.imageUrl,
                createdAt = existingItem?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val itemResult = if (itemId == null) {
                itemUseCases.add(AddItemUseCase.AddItemParams(item))
            } else {
                itemUseCases.update(UpdateItemUseCase.UpdateItemParams(item))
            }

            if (itemResult is ResultState.Success) {
                _addItemUiState.update { it.copy(isSaving = false) }
                val message = if (isEditMode) "Item updated successfully" else "Item saved successfully"
                if (!isEditMode) clearState()
                
                // Quick sync for images
                syncRepository.triggerImageSync()
                
                onSuccess(message)
            } else {
                val error = (itemResult as? ResultState.Error)?.message ?: "Unknown database error"
                _addItemUiState.update { it.copy(isSaving = false, errorMessage = "Failed to save item: $error") }
            }
        }
    }
}
