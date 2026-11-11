package com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.saifee.barcode_scanner.BarcodeScanner
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppEmptyState
import com.saifee.inventorysalesmanagement.core.ui.components.AppFloatingActionButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import androidx.compose.material.icons.outlined.ShoppingBag
import com.saifee.inventorysalesmanagement.mvvm.presentation.common.components.DeleteConfirmationDialog
import com.saifee.inventorysalesmanagement.mvvm.presentation.navigation.AppDestinations
import com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.viewmodel.PurchaseViewModel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.viewmodel.PurchaseUiState
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    viewModel: PurchaseViewModel,
    navController: NavController,
    onMenuClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PurchaseScreenContent(
        state = state,
        onMenuClick = onMenuClick,
        onNewPurchaseClick = { navController.navigate(AppDestinations.CREATE_PURCHASE.route) },
        onEditPurchaseClick = { purchase -> navController.navigate("create_purchase?purchaseId=${purchase.purchaseId}") },
        onDeletePurchaseClick = { viewModel.deletePurchase(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreenContent(
    state: PurchaseUiState,
    onMenuClick: () -> Unit,
    onNewPurchaseClick: () -> Unit,
    onEditPurchaseClick: (PurchaseEntity) -> Unit,
    onDeletePurchaseClick: (PurchaseEntity) -> Unit
) {
    var showScanner by remember { mutableStateOf(false) }
    var scannedCode by remember { mutableStateOf("") }
    var purchaseToDelete by remember { mutableStateOf<PurchaseEntity?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Purchase",
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
                onClick = onNewPurchaseClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Purchase")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.purchases.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Outlined.ShoppingBag,
                    title = "No Purchases",
                    description = "Stock up your inventory by adding a purchase",
                    actionText = "New Purchase",
                    onActionClick = onNewPurchaseClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppTokens.Spacing.Medium)
                ) {
                    items(state.purchases) { purchase ->
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
                                        AppText(text = purchase.invoiceNumber, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                        AppText(text = "PAID", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    AppText(text = "Vendor ID: ${purchase.vendorId}", style = MaterialTheme.typography.bodyMedium)
                                    AppText(text = "₹${String.format("%.2f", purchase.totalAmount)}", style = MaterialTheme.typography.bodySmall)
                                }

                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                                    }
                                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                        DropdownMenuItem(
                                            text = { AppText("Edit") },
                                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                            onClick = {
                                                expanded = false
                                                onEditPurchaseClick(purchase)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { AppText("Delete", color = MaterialTheme.colorScheme.error) },
                                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                            onClick = {
                                                expanded = false
                                                purchaseToDelete = purchase
                                            }
                                        )
                                    }
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

        purchaseToDelete?.let { purchase ->
            DeleteConfirmationDialog(
                onConfirm = {
                    onDeletePurchaseClick(purchase)
                    purchaseToDelete = null
                },
                onDismiss = { purchaseToDelete = null },
                message = "Are you sure you want to delete purchase '${purchase.invoiceNumber}'?"
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PurchaseScreenPreview() {
    InventorySalesManagementTheme {
        PurchaseScreenContent(
            state = PurchaseUiState(
                purchases = listOf(
                    PurchaseEntity(
                        purchaseId = "P1",
                        companyId = "C1",
                        locationId = "L1",
                        vendorId = "V1",
                        invoiceNumber = "PUR-001",
                        totalAmount = 5000.0,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    ),
                    PurchaseEntity(
                        purchaseId = "P2",
                        companyId = "C1",
                        locationId = "L1",
                        vendorId = "V2",
                        invoiceNumber = "PUR-002",
                        totalAmount = 7500.0,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            ),
            onMenuClick = {},
            onNewPurchaseClick = {},
            onEditPurchaseClick = {},
            onDeletePurchaseClick = {}
        )
    }
}
