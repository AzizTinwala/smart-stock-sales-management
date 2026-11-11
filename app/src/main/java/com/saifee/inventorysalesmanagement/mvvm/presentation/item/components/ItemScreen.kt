package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.saifee.barcode_scanner.BarcodeScanner
import com.saifee.inventorysalesmanagement.core.ui.components.AppEmptyState
import com.saifee.inventorysalesmanagement.core.ui.components.AppFloatingActionButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppGridListToggle
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.presentation.common.components.DeleteConfirmationDialog
import com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components.AdjustInventoryBottomSheet
import com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components.SearchSection
import com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.viewmodel.InventoryViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.presentation.navigation.AppDestinations

enum class ItemSortOption(val label: String) {
    NAME_AZ("Name (A-Z)"),
    NAME_ZA("Name (Z-A)"),
    COST_LOW_TO_HIGH("Cost: Low to High"),
    COST_HIGH_TO_LOW("Cost: High to Low"),
    SELL_LOW_TO_HIGH("Sell: Low to High"),
    SELL_HIGH_TO_LOW("Sell: High to Low"),
    STOCK_LOW_TO_HIGH("Stock: Low to High"),
    STOCK_HIGH_TO_LOW("Stock: High to Low")
}

enum class ItemFilterOption(val label: String) {
    ALL("All Items"),
    LOW_STOCK("Low Stock"),
    CRITICAL_STOCK("Critical Stock"),
    OUT_OF_STOCK("Out of Stock")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    navController: NavController,
    viewModel: InventoryViewModel,
    onMenuClick: () -> Unit,
    initialGroupId: Long? = null
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val isCompanyWise by viewModel.isCompanyWise.collectAsStateWithLifecycle()
    val adjustingItem by viewModel.adjustingItem.collectAsStateWithLifecycle()

    ItemsScreenContent(
        items = items,
        isLoading = isLoading,
        groups = groups,
        isCompanyWise = isCompanyWise,
        initialGroupId = initialGroupId,
        onMenuClick = onMenuClick,
        onBackClick = { navController.popBackStack() },
        onAddItemClick = { navController.navigate(AppDestinations.ADD_ITEM.route) },
        onEditItemClick = { item -> navController.navigate("add_item?itemId=${item.itemId}") },
        onDeleteItemClick = { viewModel.deleteItem(it) },
        onSetCompanyWise = { viewModel.setCompanyWise(it) },
        onAdjustStockClick = { item -> viewModel.setAdjustingItem(item) }
    )

    adjustingItem?.let { item ->
        AdjustInventoryBottomSheet(
            item = item,
            onDismiss = { viewModel.setAdjustingItem(null) },
            onConfirm = { qty, reason, remark ->
                viewModel.adjustStock(item.itemId, qty, reason, remark)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ItemsScreenContent(
    items: List<ItemEntity>,
    isLoading: Boolean,
    groups: List<ItemGroupEntity>,
    isCompanyWise: Boolean,
    initialGroupId: Long? = null,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onEditItemClick: (ItemEntity) -> Unit,
    onDeleteItemClick: (ItemEntity) -> Unit,
    onSetCompanyWise: (Boolean) -> Unit,
    onAdjustStockClick: (ItemEntity) -> Unit = {}
) {
    var isGrid by rememberSaveable { mutableStateOf(true) }
    var itemToDelete by remember { mutableStateOf<ItemEntity?>(null) }
    
    var search by rememberSaveable { mutableStateOf("") }
    var selectedGroupIds by rememberSaveable { 
        mutableStateOf(if (initialGroupId != null) setOf(initialGroupId) else emptySet<Long>()) 
    }
    var showScanner by remember { mutableStateOf(false) }

    var showSortSheet by remember { mutableStateOf(false) }
    var sortOption by rememberSaveable { mutableStateOf(ItemSortOption.NAME_AZ) }
    var filterOption by rememberSaveable { mutableStateOf(ItemFilterOption.ALL) }

    val filteredItems = remember(items, search, selectedGroupIds, sortOption, filterOption) {
        items.filter { item ->
            val matchesSearch = item.itemName.contains(search, ignoreCase = true) || 
                               item.sku?.contains(search, ignoreCase = true) == true ||
                               item.barcode?.contains(search, ignoreCase = true) == true
            val matchesGroup = selectedGroupIds.isEmpty() || selectedGroupIds.contains(item.groupId)
            
            val matchesFilter = when (filterOption) {
                ItemFilterOption.ALL -> true
                ItemFilterOption.LOW_STOCK -> item.stockQty > 0 && item.stockQty <= item.lowStockLevel
                ItemFilterOption.CRITICAL_STOCK -> item.stockQty > 0 && item.stockQty <= item.criticalStockLevel
                ItemFilterOption.OUT_OF_STOCK -> item.stockQty <= 0
            }
            
            matchesSearch && matchesGroup && matchesFilter
        }.let { list ->
            when (sortOption) {
                ItemSortOption.NAME_AZ -> list.sortedBy { it.itemName }
                ItemSortOption.NAME_ZA -> list.sortedByDescending { it.itemName }
                ItemSortOption.COST_LOW_TO_HIGH -> list.sortedBy { it.purchasePrice }
                ItemSortOption.COST_HIGH_TO_LOW -> list.sortedByDescending { it.purchasePrice }
                ItemSortOption.SELL_LOW_TO_HIGH -> list.sortedBy { it.sellingPrice }
                ItemSortOption.SELL_HIGH_TO_LOW -> list.sortedByDescending { it.sellingPrice }
                ItemSortOption.STOCK_LOW_TO_HIGH -> list.sortedBy { it.stockQty }
                ItemSortOption.STOCK_HIGH_TO_LOW -> list.sortedByDescending { it.stockQty }
            }
        }
    }

    val selectedGroup = if (selectedGroupIds.size == 1) groups.find { it.groupId == selectedGroupIds.first() } else null
    val titleText = if (selectedGroup != null) "Items: ${selectedGroup.groupName}" 
                    else if (selectedGroupIds.isNotEmpty()) "Items: ${selectedGroupIds.size} Groups"
                    else "Items"

    Scaffold(
        topBar = {
            AppTopBar(
                title = titleText,
                navigationIcon = if (initialGroupId != null) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                onNavigationClick = if (initialGroupId != null) onBackClick else onMenuClick
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                onClick = onAddItemClick,
            ){
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            
            SearchSection(
                value = search,
                onValueChange = { search = it },
                onBarcodeClick = { showScanner = true },
                onFilterClick = { showSortSheet = true }
            )
            
            // 🔹 Toggle UI (Moved to right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                AppGridListToggle(
                    isGrid = isGrid,
                    onToggle = { isGrid = it }
                )
            }

            // 🔹 Content
            if (filteredItems.isEmpty() && !isLoading) {
                AppEmptyState(
                    icon = Icons.Outlined.Inventory2,
                    title = "No Items Found",
                    description = if (search.isNotEmpty()) "Try a different search term" else "Start by adding your first product",
                    actionText = if (search.isEmpty()) "Add Item" else "Clear Search",
                    onActionClick = {
                        if (search.isEmpty()) onAddItemClick()
                        else search = ""
                    }
                )
            } else if (isGrid) {
                ItemGridView(
                    items = filteredItems,
                    onEdit = onEditItemClick,
                    onDelete = { item -> itemToDelete = item },
                    onAdjustStock = onAdjustStockClick
                )
            } else {
                ItemListView(
                    items = filteredItems,
                    onEdit = onEditItemClick,
                    onDelete = { item -> itemToDelete = item },
                    onAdjustStock = onAdjustStockClick
                )
            }
        }

        itemToDelete?.let { item ->
            DeleteConfirmationDialog(
                onConfirm = {
                    onDeleteItemClick(item)
                    itemToDelete = null
                },
                onDismiss = { itemToDelete = null },
                message = "Are you sure you want to delete item '${item.itemName}'?"
            )
        }
    }

    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    AppText(
                        text = "Stock View",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText("Company-wise Total Stock", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = isCompanyWise,
                            onCheckedChange = onSetCompanyWise
                        )
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                item {
                    AppText(
                        text = "Filter Stock Levels",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ItemFilterOption.entries.forEach { option ->
                            FilterChip(
                                selected = filterOption == option,
                                onClick = { filterOption = option },
                                label = { AppText(option.label) },
                                leadingIcon = if (filterOption == option) {
                                    { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                                } else null
                            )
                        }
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                item {
                    AppText(
                        text = "Filter by Groups",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groups.forEach { group ->
                            FilterChip(
                                selected = selectedGroupIds.contains(group.groupId),
                                onClick = {
                                    selectedGroupIds = if (selectedGroupIds.contains(group.groupId)) {
                                        selectedGroupIds - group.groupId
                                    } else {
                                        selectedGroupIds + group.groupId
                                    }
                                },
                                label = { AppText(group.groupName) },
                                leadingIcon = if (selectedGroupIds.contains(group.groupId)) {
                                    { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                                } else null
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                item {
                    AppText(
                        text = "Sort Items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ItemSortOption.entries.forEach { option ->
                            FilterChip(
                                selected = sortOption == option,
                                onClick = { sortOption = option },
                                label = { AppText(option.label) },
                                leadingIcon = if (sortOption == option) {
                                    { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                                } else null
                            )
                        }
                    }
                }
            }
        }
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
                        search = barcode
                        showScanner = false
                    }
                )

                IconButton(
                    onClick = { showScanner = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemsScreenPreview() {
    InventorySalesManagementTheme {
        ItemsScreenContent(
            items = listOf(
                ItemEntity(
                    itemId = 1L,
                    companyId = "C1",
                    locationId = "L1",
                    itemName = "Item A",
                    groupId = 1L,
                    sku = "SKU-A",
                    barcode = "BAR-A",
                    purchasePrice = 10.0,
                    sellingPrice = 15.0,
                    stockQty = 100.0,
                    unit = "pcs",
                    lowStockLevel = 10.0,
                    criticalStockLevel = 5.0,
                    createdAt = 0L,
                    updatedAt = 0L
                )
            ),
            isLoading = false,
            groups = listOf(
                ItemGroupEntity(
                    groupId = 1L, 
                    companyId = "C1", 
                    locationId = "L1",
                    groupName = "Group 1",
                    description = "Sample Group",
                    createdAt = 0L,
                    updatedAt = 0L
                )
            ),
            isCompanyWise = true,
            onMenuClick = {},
            onBackClick = {},
            onAddItemClick = {},
            onEditItemClick = {},
            onDeleteItemClick = {},
            onSetCompanyWise = {}
        )
    }
}
