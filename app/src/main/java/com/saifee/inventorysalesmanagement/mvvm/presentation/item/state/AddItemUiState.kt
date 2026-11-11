package com.saifee.inventorysalesmanagement.mvvm.presentation.item.state

import android.net.Uri
import com.saifee.inventorysalesmanagement.core.sealed_classes.InputState


data class AddItemUiState(

    val itemName: String = "",

    val itemGroup: String = "None",

    val costPrice: String = "",

    val sellPrice: String = "",

    val unit: String = "Pieces",

    val barcode: String = "",

    val gstPercentage: String = "",

    val hsnNumber: String = "",

    val trackInventory: Boolean = true,

    val lowStockValue: String = "",

    val criticalStockValue: String = "",

    val imageDescription: String = "",

    val imageUri: Uri? = null,
    val tempImagePath: String? = null,

    val isSaving: Boolean = false,
    val itemNameInputState: InputState = InputState.Normal,

    val itemNameError: String? = null,
    val itemGroupInputState: InputState = InputState.Normal,

    val itemGroupError: String? = null,
    val costPriceInputState: InputState = InputState.Normal,

    val costPriceError: String? = null,
    
    val availableGroups: List<String> = emptyList(),
    val errorMessage: String? = null,
    val showAddGroupDialog: Boolean = false,
    val dialogGroupName: String = "",
    val dialogGroupDescription: String = "",
    val dialogGroupNameError: String? = null,
    val isDialogSaving: Boolean = false
)
