package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.StockStatus
import com.saifee.inventorysalesmanagement.core.ui.components.StockStatusBadge
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import java.util.Locale

@Composable
fun ItemListView(
    items: List<ItemEntity>,
    onEdit: (ItemEntity) -> Unit,
    onDelete: (ItemEntity) -> Unit,
    onAdjustStock: (ItemEntity) -> Unit = {}
) {
    val cardShape = RoundedCornerShape(AppTokens.CornerRadius.Card)
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = AppTokens.Spacing.PagePadding, vertical = 12.dp)
    ) {
        items(items) { item ->
            val stockStatus = when {
                item.stockQty <= 0 -> StockStatus.OUT_OF_STOCK
                item.stockQty <= item.lowStockLevel -> StockStatus.LOW_STOCK
                else -> StockStatus.IN_STOCK
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = AppTokens.Dimensions.BorderWidth, color = borderColor, shape = cardShape),
                onClick = { onEdit(item) },
                shape = cardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = AppTokens.Elevation.None)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = item.itemName,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.itemName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "SKU: ${item.sku ?: "N/A"} • ${item.stockQty.toInt()} ${item.unit.orEmpty().ifEmpty { "pcs" }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(4.dp))
                        StockStatusBadge(status = stockStatus)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${String.format(Locale.getDefault(), "%.2f", item.sellingPrice)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onAdjustStock(item) }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Adjust Stock",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { onEdit(item) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
