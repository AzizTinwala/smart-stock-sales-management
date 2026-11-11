package com.saifee.inventorysalesmanagement.mvvm.presentation.sales.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.saifee.barcode_scanner.BarcodeScanner
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppEmptyState
import com.saifee.inventorysalesmanagement.core.ui.components.AppFloatingActionButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.mvvm.presentation.navigation.AppDestinations
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.presentation.common.components.DeleteConfirmationDialog
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.viewmodel.SalesViewModel
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.viewmodel.SalesUiState
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    navController: NavController,
    viewModel: SalesViewModel,
    onMenuClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SalesScreenContent(
        state = state,
        onMenuClick = onMenuClick,
        onNewSaleClick = { navController.navigate(AppDestinations.CREATE_SALE.route) },
        onEditSaleClick = { sale -> navController.navigate("create_sale?salesId=${sale.salesId}") },
        onDeleteSaleClick = { viewModel.deleteSale(it) },
        onViewInvoiceClick = { sale -> navController.navigate("invoice_preview?salesId=${sale.salesId}") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreenContent(
    state: SalesUiState,
    onMenuClick: () -> Unit,
    onNewSaleClick: () -> Unit,
    onEditSaleClick: (SalesEntity) -> Unit,
    onDeleteSaleClick: (SalesEntity) -> Unit,
    onViewInvoiceClick: (SalesEntity) -> Unit
) {
    var showScanner by remember { mutableStateOf(false) }
    var scannedCode by remember { mutableStateOf("") }
    var saleToDelete by remember { mutableStateOf<SalesEntity?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Sales",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick,
                actions = {
                    IconButton(onClick = { showScanner = true }) {
                        Icon(Icons.Rounded.QrCodeScanner, contentDescription = "Scan")
                    }
                }
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                onClick = onNewSaleClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Sale")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.sales.isEmpty()) {
            AppEmptyState(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                title = "No Sales Yet",
                description = "Record your first sale to see it here",
                actionText = "Create Sale",
                onActionClick = onNewSaleClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppTokens.Spacing.Medium)
            ) {
                items(state.sales) { sale ->
                    AppCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTokens.Spacing.Small)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    AppText(text = sale.invoiceNumber, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                    AppText(text = "₹${sale.finalAmount}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                }
                                AppText(text = "Customer ID: ${sale.customerId ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                                AppText(text = "Date: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(sale.createdAt))}", style = MaterialTheme.typography.bodySmall)
                            }

                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(
                                        text = { AppText("View Invoice") },
                                        leadingIcon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                                        onClick = {
                                            expanded = false
                                            onViewInvoiceClick(sale)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { AppText("Edit") },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                        onClick = {
                                            expanded = false
                                            onEditSaleClick(sale)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { AppText("Delete", color = MaterialTheme.colorScheme.error) },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            expanded = false
                                            saleToDelete = sale
                                        }
                                    )
                                }
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
                            scannedCode = barcode
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

        saleToDelete?.let { sale ->
            DeleteConfirmationDialog(
                onConfirm = {
                    onDeleteSaleClick(sale)
                    saleToDelete = null
                },
                onDismiss = { saleToDelete = null },
                message = "Are you sure you want to delete sale '${sale.invoiceNumber}'?"
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SalesScreenPreview() {
    InventorySalesManagementTheme {
        SalesScreenContent(
            state = SalesUiState(
                sales = listOf(
                    SalesEntity(
                        salesId = "S1",
                        invoiceNumber = "INV-001",
                        customerId = "C1",
                        totalAmount = 1500.0,
                        discount = 0.0,
                        tax = 0.0,
                        finalAmount = 1500.0,
                        status = "PAID",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                        companyId = "C1",
                        locationId = "L1"
                    ),
                    SalesEntity(
                        salesId = "S2",
                        invoiceNumber = "INV-002",
                        customerId = "C2",
                        totalAmount = 2500.0,
                        discount = 0.0,
                        tax = 0.0,
                        finalAmount = 2500.0,
                        status = "PAID",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                        companyId = "C1",
                        locationId = "L1"
                    )
                )
            ),
            onMenuClick = {},
            onNewSaleClick = {},
            onEditSaleClick = {},
            onDeleteSaleClick = {},
            onViewInvoiceClick = {}
        )
    }
}
