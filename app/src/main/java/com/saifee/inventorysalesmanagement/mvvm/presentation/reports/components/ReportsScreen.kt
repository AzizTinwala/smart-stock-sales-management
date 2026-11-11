package com.saifee.inventorysalesmanagement.mvvm.presentation.reports.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.mvvm.presentation.reports.viewmodel.ReportsViewModel
import java.util.Locale
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.domain.model.ReportStats
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onMenuClick: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    ReportsScreenContent(
        stats = stats,
        isLoading = isLoading,
        onMenuClick = onMenuClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreenContent(
    stats: ReportStats,
    isLoading: Boolean,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Business Reports",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick
            )
        }
    ) { padding ->
        if (isLoading && stats.monthlySalesActual == 0.0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ReportCard(
                        title = "Monthly Sales Goal",
                        value = "₹${String.format(Locale.getDefault(), "%.2f", stats.monthlySalesActual)} / ₹${String.format(Locale.getDefault(), "%.2f", stats.monthlySalesGoal)}",
                        progress = stats.salesProgress
                    )
                }

                item {
                    ReportCard(
                        title = "Inventory Turnover",
                        value = String.format(Locale.getDefault(), "%.2fx", stats.inventoryTurnover),
                        progress = (stats.inventoryTurnover / 10).toFloat().coerceIn(0f, 1f) // Example scaling
                    )
                }

                item {
                    ReportCard(
                        title = "Current Stock Value",
                        value = "₹${String.format(Locale.getDefault(), "%.2f", stats.totalStockValue)}",
                        progress = 1f, // Static value
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                
                item {
                    ReportCard(
                        title = "Total Procurement",
                        value = "₹${String.format(Locale.getDefault(), "%.2f", stats.totalPurchaseCost)}",
                        progress = 0.5f,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    title: String,
    value: String,
    progress: Float,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReportsScreenPreview() {
    InventorySalesManagementTheme {
        ReportsScreenContent(
            stats = ReportStats(
                monthlySalesActual = 4500.0,
                monthlySalesGoal = 10000.0,
                totalStockValue = 125000.0,
                totalPurchaseCost = 85000.0,
                inventoryTurnover = 2.4
            ),
            isLoading = false,
            onMenuClick = {}
        )
    }
}
