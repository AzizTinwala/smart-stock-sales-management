package com.saifee.inventorysalesmanagement.mvvm.presentation.ledger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.components.AppAutoCompleteTextView
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerEntry
import com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerTransactionType
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyLedgerScreen(
    viewModel: PartyLedgerViewModel,
    onMenuClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    PartyLedgerScreenContent(
        state = state,
        onMenuClick = onMenuClick,
        onTogglePartyType = viewModel::onTogglePartyType,
        onPartySelected = viewModel::onPartySelected,
        onDateRangeSelected = viewModel::onDateRangeSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyLedgerScreenContent(
    state: PartyLedgerUiState,
    onMenuClick: () -> Unit,
    onTogglePartyType: (Boolean) -> Unit,
    onPartySelected: (String, String) -> Unit,
    onDateRangeSelected: (Long?, Long?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Statement of Accounts",
                navigationIcon = Icons.Default.Menu,
                onNavigationClick = onMenuClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Segmented Controller for Customer vs Vendor Ledger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.isCustomerView,
                    onClick = { onTogglePartyType(true) },
                    label = { AppText("Customer Statement", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = !state.isCustomerView,
                    onClick = { onTogglePartyType(false) },
                    label = { AppText("Vendor Statement", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Party Selection Dropdown Field
            val activeParties = if (state.isCustomerView) state.customers.map { it.customerId to it.name }
                                else state.vendors.map { it.vendorId to it.name }

            AppAutoCompleteTextView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small),
                label = if (state.isCustomerView) "Select Customer *" else "Select Vendor *",
                value = state.partyName,
                items = activeParties.map { it.second },
                onItemSelected = { name ->
                    val selectedId = activeParties.find { it.second == name }?.first ?: ""
                    onPartySelected(selectedId, name)
                },
                placeholder = "Search account ledger..."
            )

            // Date Range Action Picker row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.ExtraSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dateLabel = if (state.startDate != null && state.endDate != null) {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    "${sdf.format(Date(state.startDate))} - ${sdf.format(Date(state.endDate))}"
                } else {
                    "All Time Records"
                }

                AppText(
                    text = dateLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Choose Dates")
                }
            }

            // Summary Card Widgets (KPI blocks matching Payment screen design tokens)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small),
                horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Small)
            ) {
                // Total Invoiced Card
                AppCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(AppTokens.Spacing.Small)) {
                        AppText(
                            text = if (state.isCustomerView) "Total Billed" else "Total Inward",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText(
                            text = "₹${String.format(Locale.getDefault(), "%.2f", state.totalInvoicedAmount)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Total Settled/Paid Card
                AppCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F8EC))
                ) {
                    Column(modifier = Modifier.padding(AppTokens.Spacing.Small)) {
                        AppText(
                            text = if (state.isCustomerView) "Settled" else "Settled Payouts",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText(
                            text = "₹${String.format(Locale.getDefault(), "%.2f", state.totalSettledAmount)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Balance Outstanding Card
                val balColor = if (state.closingBalance >= 0) Color(0xFFC62828) else Color(0xFF2E7D32)
                val balBg = if (state.closingBalance >= 0) Color(0xFFFEEBEE) else Color(0xFFE8F8EC)
                AppCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = balBg)
                ) {
                    Column(modifier = Modifier.padding(AppTokens.Spacing.Small)) {
                        AppText(
                            text = "Net Balance",
                            style = MaterialTheme.typography.labelSmall,
                            color = balColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText(
                            text = "₹${String.format(Locale.getDefault(), "%.2f", Math.abs(state.closingBalance))}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = balColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Ledger Statement Line Items Headers Row
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppTokens.Spacing.Medium, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppText("Date / Particulars", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                    AppText("Debit", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    AppText("Credit", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    AppText("Balance", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold)
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.selectedPartyId.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppText("Please select a ledger account to view details.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (state.ledgerEntries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppText("No statement entries recorded for selected parameters.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = AppTokens.Spacing.Medium, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.ledgerEntries) { item ->
                        AppCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Date & Particulars Info Column
                                Column(modifier = Modifier.weight(2f)) {
                                    val entryDateStr = if (item.date == 0L) "Beginning" else SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(item.date))
                                    AppText(text = entryDateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    AppText(text = item.type.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    AppText(text = "Ref: ${item.referenceNumber}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                }

                                // Debit Value Column
                                val debStr = if (item.debitAmount > 0) "₹${String.format(Locale.getDefault(), "%.2f", item.debitAmount)}" else "—"
                                AppText(
                                    text = debStr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    color = if (item.debitAmount > 0) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Credit Value Column
                                val credStr = if (item.creditAmount > 0) "₹${String.format(Locale.getDefault(), "%.2f", item.creditAmount)}" else "—"
                                AppText(
                                    text = credStr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    color = if (item.creditAmount > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Running Statement Cumulative Balance Column
                                AppText(
                                    text = "₹${String.format(Locale.getDefault(), "%.2f", Math.abs(item.runningBalance))}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Material 3 Range Picker Dialog popup overlay
    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DateRangePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateRangeSelected(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                        showDatePicker = false
                    }
                ) {
                    AppText("Apply Range")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDateRangeSelected(null, null)
                    showDatePicker = false
                }) {
                    AppText("Clear Filters")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().height(450.dp),
                content = content
            )
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PartyLedgerScreenPreview() {
    InventorySalesManagementTheme {
        PartyLedgerScreenContent(
            state = PartyLedgerUiState(
                partyName = "Sample Enterprise Client",
                selectedPartyId = "123",
                ledgerEntries = listOf(
                    LedgerEntry("1", System.currentTimeMillis(), LedgerTransactionType.OPENING_BALANCE, "OB-01", "OB", 1000.0, 0.0, 1000.0),
                    LedgerEntry("2", System.currentTimeMillis(), LedgerTransactionType.SALE, "INV-992", "Invoice sale", 450.0, 0.0, 1450.0),
                    LedgerEntry("3", System.currentTimeMillis(), LedgerTransactionType.PAYMENT_RECEIVED, "TXN-881", "Payment received", 0.0, 1450.0, 0.0)
                )
            ),
            onMenuClick = {},
            onTogglePartyType = {},
            onPartySelected = { _, _ -> },
            onDateRangeSelected = { _, _ -> }
        )
    }
}
