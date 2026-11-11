package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saifee.barcode_scanner.BarcodeScanner
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppFormColumn
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.viewmodel.AddItemViewModel
import kotlinx.coroutines.launch

import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.AddItemUiState

@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.addItemUiState.collectAsStateWithLifecycle()

    AddItemScreenContent(
        state = state,
        isEditMode = viewModel.isEditMode,
        onBack = onBack,
        onImageSelected = viewModel::onImageSelected,
        onSaveItem = { callback ->
            viewModel.saveItem { message ->
                callback(message)
                onBack()
            }
        },
        onItemNameChange = viewModel::onItemNameChange,
        onGroupSelected = viewModel::onItemGroupChange,
        onAddGroupClick = { viewModel.setShowAddGroupDialog(true) },
        onCostPriceChange = viewModel::onCostPriceChange,
        onSellPriceChange = viewModel::onSellPriceChange,
        onGstPercentageChange = viewModel::onGstPercentageChange,
        onHsnNumberChange = viewModel::onHsnNumberChange,
        onUnitSelected = viewModel::onUnitChange,
        onBarcodeChange = viewModel::onBarcodeChange,
        onTrackInventoryChange = viewModel::onTrackInventoryChanged,
        onLowStockChange = viewModel::onLowStockValueChange,
        onCriticalStockChange = viewModel::onCriticalStockValueChange,
        onGenerateBarcode = viewModel::onGenerateBarcode,
        onDialogGroupNameChange = viewModel::onDialogGroupNameChange,
        onDialogGroupDescriptionChange = viewModel::onDialogGroupDescriptionChange,
        onDismissGroupDialog = { viewModel.setShowAddGroupDialog(false) },
        onSaveNewGroup = { viewModel.addNewGroup() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreenContent(
    state: AddItemUiState,
    isEditMode: Boolean,
    onBack: () -> Unit,
    onImageSelected: (android.net.Uri?) -> Unit,
    onSaveItem: ((String) -> Unit) -> Unit,
    onItemNameChange: (String) -> Unit,
    onGroupSelected: (String) -> Unit,
    onAddGroupClick: () -> Unit,
    onCostPriceChange: (String) -> Unit,
    onSellPriceChange: (String) -> Unit,
    onGstPercentageChange: (String) -> Unit,
    onHsnNumberChange: (String) -> Unit,
    onUnitSelected: (String) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onTrackInventoryChange: (Boolean) -> Unit,
    onLowStockChange: (String) -> Unit,
    onCriticalStockChange: (String) -> Unit,
    onGenerateBarcode: () -> Unit,
    onDialogGroupNameChange: (String) -> Unit,
    onDialogGroupDescriptionChange: (String) -> Unit,
    onDismissGroupDialog: () -> Unit,
    onSaveNewGroup: () -> Unit
) {
    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            onImageSelected(uri)
        }

    var visible by remember {
        mutableStateOf(false)
    }
    var showScanner by remember { mutableStateOf(false) }
    
    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    val scope =
        rememberCoroutineScope()
    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = if (isEditMode) "Edit Item" else "Add Item",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        },

        bottomBar = {
            Surface(
                shadowElevation = AppTokens.Elevation.High
            ) {
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTokens.Spacing.Medium),
                    enabled = !state.isSaving,
                    text = if (isEditMode) "Update Item" else "Save Item",
                    isLoading = state.isSaving,
                    onClick = {
                        onSaveItem { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    }
                )
            }
        }

    ) { padding ->

        AnimatedVisibility(

            visible = visible,

            enter = fadeIn() +
                    slideInVertically { it / 4 }

        ) {

            AppFormColumn(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = AppTokens.Spacing.PagePadding),

                verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)

            ) {

                BasicSpecificationSection(
                    state = state,
                    onItemNameChange = onItemNameChange,
                    onGroupSelected = onGroupSelected,
                    onAddGroupClick = onAddGroupClick
                )
                PricingSection(
                    state = state,
                    onCostPriceChange = onCostPriceChange,
                    onSellPriceChange = onSellPriceChange,
                    onGstPercentageChange = onGstPercentageChange,
                    onHsnNumberChange = onHsnNumberChange
                )

                UnitTrackingSection(
                    state = state,
                    onUnitSelected = onUnitSelected,
                    onBarcodeChange = onBarcodeChange,
                    onTrackInventoryChange = onTrackInventoryChange,
                    onLowStockChange = onLowStockChange,
                    onCriticalStockChange = onCriticalStockChange,
                    onScanBarcode = {
                        showScanner = true
                    },
                    onGenerateBarcode = onGenerateBarcode
                )

                ItemImageSection(
                    state = state,
                    onPickImage = {
                        galleryLauncher.launch("image/*")
                    },
                    onRemoveImage = {
                        onImageSelected(null)
                    }
                )

                Spacer(modifier = Modifier.height(100.dp))

            }

        }

    }

    if (state.showAddGroupDialog) {
        ItemGroupBottomSheet(
            isEditMode = false,
            groupName = state.dialogGroupName,
            onGroupNameChange = onDialogGroupNameChange,
            groupDescription = state.dialogGroupDescription,
            onGroupDescriptionChange = onDialogGroupDescriptionChange,
            groupNameError = state.dialogGroupNameError,
            isSaving = state.isDialogSaving,
            errorMessage = state.errorMessage,
            onDismiss = onDismissGroupDialog,
            onSave = onSaveNewGroup
        )
    }

    if (showScanner) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showScanner = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                BarcodeScanner(
                    onBarcodeDetected = { barcode ->
                        onBarcodeChange(barcode)
                        showScanner = false
                    }
                )
                
                IconButton(
                    onClick = { showScanner = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AddItemScreenPreview() {
    InventorySalesManagementTheme {
        AddItemScreenContent(
            state = AddItemUiState(),
            isEditMode = false,
            onBack = {},
            onImageSelected = {},
            onSaveItem = {},
            onItemNameChange = {},
            onGroupSelected = {},
            onAddGroupClick = {},
            onCostPriceChange = {},
            onSellPriceChange = {},
            onGstPercentageChange = {},
            onHsnNumberChange = {},
            onUnitSelected = {},
            onBarcodeChange = {},
            onTrackInventoryChange = {},
            onLowStockChange = {},
            onCriticalStockChange = {},
            onGenerateBarcode = {},
            onDialogGroupNameChange = {},
            onDialogGroupDescriptionChange = {},
            onDismissGroupDialog = {},
            onSaveNewGroup = {}
        )
    }
}


