package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.components.StockSummaryCard
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.theme.stockColors
import com.saifee.inventorysalesmanagement.mvvm.domain.model.DashboardStats
import com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.viewmodel.DashboardUiState
import com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.viewmodel.DashboardViewModel
import java.util.Locale

@Composable
fun DashboardContent(
    viewModel: DashboardViewModel,
    onNavigateTo: (String) -> Unit,
    onScanBarcodeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    DashboardContent(
        uiState = uiState,
        onNavigateTo = onNavigateTo,
        onScanBarcodeClick = onScanBarcodeClick,
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onNavigateTo: (String) -> Unit = {},
    onScanBarcodeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stockColors = MaterialTheme.colorScheme.stockColors
    val stats = uiState.stats

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 100.dp,
            start = AppTokens.Spacing.PagePadding,
            end = AppTokens.Spacing.PagePadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
    ) {
        // 1. Header
        item {
            DashboardHeader(
                userName = uiState.userName,
                companyName = uiState.companyName,
                warehouseName = uiState.warehouseName
            )
        }

        // 2. Metrics Stat Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StockSummaryCard(
                        title = "Total Products",
                        value = String.format(Locale.getDefault(), "%,.0f", stats.inventoryCount),
                        subtitle = "In 12 Categories",
                        icon = Icons.Default.Inventory2,
                        modifier = Modifier.weight(1f)
                    )
                    StockSummaryCard(
                        title = "Stock Value",
                        value = "$${String.format(Locale.getDefault(), "%,.2f", stats.totalStockValue)}",
                        subtitle = "Main Warehouse",
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StockSummaryCard(
                        title = "Low Stock Items",
                        value = "${stats.lowStockCount} items",
                        subtitle = "Requires Reorder",
                        icon = Icons.Default.Warning,
                        iconContainerColor = stockColors.lowStockContainer,
                        iconColor = stockColors.onLowStockContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StockSummaryCard(
                        title = "Out of Stock",
                        value = "${stats.outOfStockCount} items",
                        subtitle = "Urgent Restock",
                        icon = Icons.Default.Error,
                        iconContainerColor = stockColors.outOfStockContainer,
                        iconColor = stockColors.onOutOfStockContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Quick Action Hub
        item {
            QuickActionHub(
                onAddProductClick = { onNavigateTo("add_item") },
                onStockInClick = { onNavigateTo("purchase") },
                onStockOutClick = { onNavigateTo("sales") },
                onScanBarcodeClick = onScanBarcodeClick
            )
        }

        // 4. Stock Distribution Overview
        item {
            StockDistributionChart(
                totalItems = stats.inventoryCount.toInt(),
                lowStockItems = stats.lowStockCount,
                outOfStockItems = stats.outOfStockCount
            )
        }

        // 5. Recent Stock Movements Timeline
        item {
            RecentStockMovements(
                movements = emptyList(),
                onViewAllClick = { onNavigateTo("inventory") }
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(AppTokens.Spacing.Large)
                    .navigationBarsPadding()
            )
        }
    }
}
