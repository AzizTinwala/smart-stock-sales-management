package com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.saifee.inventorysalesmanagement.core.ui.components.AppFloatingActionButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.viewmodel.DashboardUiState
import com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onMenuClick: () -> Unit,
    onFabClick: () -> Unit = {},
    onNavigateTo: (String) -> Unit = {},
    onScanBarcodeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    DashboardScreenContent(
        uiState = uiState,
        onMenuClick = onMenuClick,
        onFabClick = onFabClick,
        onNavigateTo = onNavigateTo,
        onScanBarcodeClick = onScanBarcodeClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onMenuClick: () -> Unit,
    onFabClick: () -> Unit = {},
    onNavigateTo: (String) -> Unit = {},
    onScanBarcodeClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Dashboard",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick,
                companyName = uiState.companyName,
                warehouseName = uiState.warehouseName,
                onContextSwitchClick = onMenuClick,
                onSearchClick = { onNavigateTo("items") },
                onScanClick = onScanBarcodeClick
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add Product"
                )
            }
        }
    ) { padding ->
        DashboardContent(
            uiState = uiState,
            onNavigateTo = onNavigateTo,
            onScanBarcodeClick = onScanBarcodeClick,
            modifier = Modifier.padding(padding)
        )
    }
}
