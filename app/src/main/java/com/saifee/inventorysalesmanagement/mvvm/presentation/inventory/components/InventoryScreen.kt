package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.barcode_scanner.BarcodeScanner
import com.saifee.inventorysalesmanagement.core.ui.components.AppEmptyState
import com.saifee.inventorysalesmanagement.core.ui.components.AppFloatingActionButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.components.StatusBadge
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.theme.stockColors
import com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.viewmodel.InventoryViewModel
import java.util.Locale

enum class MovementTypeFilter(val label: String) {
    ALL("All Movements"),
    STOCK_IN("Stock In"),
    STOCK_OUT("Stock Out")
}

/**
 * Screen 8: StockPilot Transaction History & Audit Trail
 */
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onMenuClick: () -> Unit,
    onAddClick: () -> Unit
) {
    InventoryScreenContent(
        onMenuClick = onMenuClick,
        onAddClick = onAddClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreenContent(
    onMenuClick: () -> Unit,
    onAddClick: () -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(MovementTypeFilter.ALL) }
    var showScanner by remember { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<TransactionRecord?>(null) }

    val sampleRecords = remember {
        emptyList<TransactionRecord>()
    }

    val filteredRecords = remember(search, selectedFilter, sampleRecords) {
        sampleRecords.filter { record ->
            val matchesSearch = record.productName.contains(search, ignoreCase = true) ||
                               record.sku.contains(search, ignoreCase = true) ||
                               record.referenceNo.contains(search, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                MovementTypeFilter.ALL -> true
                MovementTypeFilter.STOCK_IN -> record.isStockIn
                MovementTypeFilter.STOCK_OUT -> !record.isStockIn
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Transaction History",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchSection(
                value = search,
                onValueChange = { search = it },
                onBarcodeClick = { showScanner = true },
                onFilterClick = {}
            )

            // Movement Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTokens.Spacing.PagePadding, vertical = 8.dp)
            ) {
                MovementTypeFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label) },
                        leadingIcon = if (selectedFilter == filter) {
                            { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (filteredRecords.isEmpty()) {
                AppEmptyState(
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    title = if (search.isEmpty()) "No Transactions" else "No matching transactions",
                    description = if (search.isEmpty()) "Stock In and Stock Out records will appear here" else "Try a different search term",
                    actionText = if (search.isEmpty()) "Stock In" else "Clear Search",
                    onActionClick = {
                        if (search.isEmpty()) onAddClick()
                        else search = ""
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = AppTokens.Spacing.PagePadding, vertical = 8.dp)
                ) {
                    items(filteredRecords, key = { it.transactionId }) { record ->
                        TransactionRecordCard(
                            record = record,
                            onClick = { selectedRecord = record }
                        )
                    }
                }
            }
        }
    }

    selectedRecord?.let { record ->
        TransactionDetailsBottomSheet(
            record = record,
            onDismiss = { selectedRecord = null }
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

@Composable
private fun TransactionRecordCard(
    record: TransactionRecord,
    onClick: () -> Unit
) {
    val stockColors = MaterialTheme.colorScheme.stockColors
    val cardShape = RoundedCornerShape(AppTokens.CornerRadius.Card)
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    val (badgeLabel, containerColor, contentColor) = if (record.isStockIn) {
        Triple("Stock In", stockColors.inStockContainer, stockColors.onInStockContainer)
    } else {
        Triple("Stock Out", stockColors.outOfStockContainer, stockColors.onOutOfStockContainer)
    }

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTokens.Elevation.None),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = AppTokens.Dimensions.BorderWidth, color = borderColor, shape = cardShape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTokens.Spacing.Medium)
        ) {
            StatusBadge(
                label = badgeLabel,
                containerColor = containerColor,
                contentColor = contentColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.productName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${record.referenceNo} • ${record.sku}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "${record.operatorName} • ${record.date}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (record.isStockIn) "+${record.quantity} pcs" else "-${record.quantity} pcs",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (record.isStockIn) stockColors.inStock else stockColors.outOfStock
                )
                Text(
                    text = "$${String.format(Locale.getDefault(), "%.2f", record.quantity * record.unitPrice)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
