package com.saifee.inventorysalesmanagement.mvvm.presentation.payments.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.PaymentType
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppEmptyState
import com.saifee.inventorysalesmanagement.core.ui.components.AppFloatingActionButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.mvvm.presentation.payments.viewmodel.PaymentsViewModel
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import java.text.SimpleDateFormat
import java.util.*
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.presentation.payments.viewmodel.PaymentsUiState
import androidx.compose.ui.tooling.preview.Preview
import com.saifee.inventorysalesmanagement.core.enums.PaymentMode

import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    viewModel: PaymentsViewModel,
    onMenuClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    PaymentsScreenContent(
        state = state,
        onMenuClick = onMenuClick,
        onAddPaymentClick = { viewModel.setShowAddDialog(true) },
        onDismissDialog = { viewModel.setShowAddDialog(false) },
        onSavePayment = viewModel::addPayment,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onFilterTypeChange = viewModel::onFilterTypeChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreenContent(
    state: PaymentsUiState,
    onMenuClick: () -> Unit,
    onAddPaymentClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onSavePayment: (PaymentType, String, Double, PaymentMode, String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterTypeChange: (String) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Payments & Ledger",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(onClick = onAddPaymentClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Payment")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // KPI Summary Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTokens.Spacing.Medium)
                    .padding(top = AppTokens.Spacing.Medium),
                horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small)
            ) {
                // Receipts Card
                AppCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F8EC))
                ) {
                    Column(modifier = Modifier.padding(AppTokens.Spacing.Small)) {
                        AppText(
                            text = "Receipts",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText(
                            text = "+₹${String.format(Locale.getDefault(), "%.2f", state.totalReceived)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Payouts Card
                AppCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEEBEE))
                ) {
                    Column(modifier = Modifier.padding(AppTokens.Spacing.Small)) {
                        AppText(
                            text = "Payouts",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText(
                            text = "-₹${String.format(Locale.getDefault(), "%.2f", state.totalPaid)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Net Cash Card
                AppCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(AppTokens.Spacing.Small)) {
                        AppText(
                            text = "Net Cash",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText(
                            text = "₹${String.format(Locale.getDefault(), "%.2f", state.netCash)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Search Bar
            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small),
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Search payments by contact or reference...",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            )

            // Filter Tabs / Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTokens.Spacing.Medium)
                    .padding(bottom = AppTokens.Spacing.Small),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.filterType == "ALL",
                    onClick = { onFilterTypeChange("ALL") },
                    label = { AppText("All Payments", style = MaterialTheme.typography.bodyMedium) }
                )
                FilterChip(
                    selected = state.filterType == "RECEIVED",
                    onClick = { onFilterTypeChange("RECEIVED") },
                    label = { AppText("Received", style = MaterialTheme.typography.bodyMedium) }
                )
                FilterChip(
                    selected = state.filterType == "PAID",
                    onClick = { onFilterTypeChange("PAID") },
                    label = { AppText("Paid Out", style = MaterialTheme.typography.bodyMedium) }
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.payments.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    title = "No Payments",
                    description = "Your payment history will appear here",
                    actionText = "Add Payment",
                    onActionClick = onAddPaymentClick
                )
            } else if (state.filteredPayments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppText("No matching transactions found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppTokens.Spacing.Medium),
                    verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small)
                ) {
                    items(state.filteredPayments) { payment ->
                        val isReceived = payment.type == PaymentType.OUT
                        val baseColor = if (isReceived) Color(0xFF2E7D32) else Color(0xFFC62828)
                        val containerColor = if (isReceived) Color(0xFFE8F8EC) else Color(0xFFFEEBEE)

                        AppCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = containerColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(AppTokens.Spacing.Medium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Direction Icon Circle
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = baseColor.copy(alpha = 0.15f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isReceived) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = baseColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(AppTokens.Spacing.Medium))

                                Column(modifier = Modifier.weight(1f)) {
                                    AppText(
                                        text = payment.partyName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(payment.date))
                                    AppText(
                                        text = "${payment.mode} • $dateStr",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (!payment.reference.isNullOrBlank()) {
                                        AppText(
                                            text = "Ref: ${payment.reference}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    val amountStr = String.format(Locale.getDefault(), "%.2f", payment.amount)
                                    AppText(
                                        text = "${if (isReceived) "+" else "-"}₹$amountStr",
                                        color = baseColor,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    AppText(
                                        text = if (isReceived) "Received from client" else "Paid to vendor",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showAddDialog) {
        AddPaymentBottomSheet(
            customers = state.customers,
            vendors = state.vendors,
            onDismiss = onDismissDialog,
            onSave = onSavePayment
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PaymentsScreenPreview() {
    InventorySalesManagementTheme {
        PaymentsScreenContent(
            state = PaymentsUiState(
                payments = listOf(
                    com.saifee.inventorysalesmanagement.mvvm.presentation.payments.viewmodel.PaymentUiItem(
                        id = "1",
                        partyName = "Customer A",
                        amount = 1200.0,
                        type = PaymentType.OUT,
                        mode = "CASH",
                        date = System.currentTimeMillis(),
                        reference = null
                    ),
                    com.saifee.inventorysalesmanagement.mvvm.presentation.payments.viewmodel.PaymentUiItem(
                        id = "2",
                        partyName = "Vendor B",
                        amount = 5000.0,
                        type = PaymentType.IN,
                        mode = "BANK",
                        date = System.currentTimeMillis(),
                        reference = "REF123"
                    )
                )
            ),
            onMenuClick = {},
            onAddPaymentClick = {},
            onDismissDialog = {},
            onSavePayment = { _, _, _, _, _ -> },
            onSearchQueryChange = {},
            onFilterTypeChange = {}
        )
    }
}
