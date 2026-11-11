package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.StockStatus
import com.saifee.inventorysalesmanagement.core.ui.components.StatusBadge
import com.saifee.inventorysalesmanagement.core.ui.components.StockStatusBadge
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.theme.stockColors
import java.util.Locale

data class TransactionRecord(
    val transactionId: String,
    val referenceNo: String,
    val productName: String,
    val sku: String,
    val isStockIn: Boolean,
    val quantity: Int,
    val unitPrice: Double,
    val warehouseName: String,
    val operatorName: String,
    val date: String,
    val notes: String? = null
)

/**
 * Detailed Transaction Audit Modal Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsBottomSheet(
    record: TransactionRecord,
    onDismiss: () -> Unit
) {
    val stockColors = MaterialTheme.colorScheme.stockColors
    val totalAmount = record.quantity * record.unitPrice

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Transaction Audit",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Reference: ${record.referenceNo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(
                    label = if (record.isStockIn) "Stock In (Received)" else "Stock Out (Issued)",
                    containerColor = if (record.isStockIn) stockColors.inStockContainer else stockColors.outOfStockContainer,
                    contentColor = if (record.isStockIn) stockColors.onInStockContainer else stockColors.onOutOfStockContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            DetailRow("Product Name", record.productName)
            DetailRow("SKU Code", record.sku)
            DetailRow("Warehouse Location", record.warehouseName)
            DetailRow("Quantity Change", if (record.isStockIn) "+${record.quantity} pcs" else "-${record.quantity} pcs")
            DetailRow("Unit Rate", "$${String.format(Locale.getDefault(), "%.2f", record.unitPrice)}")
            DetailRow("Total Movement Valuation", "$${String.format(Locale.getDefault(), "%.2f", totalAmount)}", isHighlight = true)
            DetailRow("Operator", record.operatorName)
            DetailRow("Timestamp", record.date)

            if (!record.notes.isNullOrEmpty()) {
                DetailRow("Notes", record.notes)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
