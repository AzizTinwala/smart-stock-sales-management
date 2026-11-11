package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.StockStatus
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.components.StatusBadge
import com.saifee.inventorysalesmanagement.core.ui.components.StockStatusBadge
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import java.util.Locale

/**
 * Screen 3: StockPilot Product Details View
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    item: ItemEntity,
    categoryName: String = "General Hardware",
    onBack: () -> Unit,
    onEdit: (ItemEntity) -> Unit,
    onStockIn: (ItemEntity) -> Unit,
    onStockOut: (ItemEntity) -> Unit
) {
    val stockStatus = when {
        item.stockQty <= 0 -> StockStatus.OUT_OF_STOCK
        item.stockQty <= item.lowStockLevel -> StockStatus.LOW_STOCK
        else -> StockStatus.IN_STOCK
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Product Details",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                actions = {
                    IconButton(onClick = { onEdit(item) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Product",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppTokens.Spacing.Medium, vertical = AppTokens.Spacing.Small),
            verticalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
        ) {
            // 1. Hero Product Card
            HeroProductCard(item = item, categoryName = categoryName, stockStatus = stockStatus)

            // 2. Available Stock & Thresholds
            StockThresholdCard(item = item, stockStatus = stockStatus)

            // 3. Pricing Breakdown Card
            PricingBreakdownCard(item = item)

            // 4. Warehouse Allocation
            WarehouseAllocationCard(item = item)

            // 5. Quick Actions Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AppButton(
                    text = "Stock In",
                    icon = Icons.Default.AddBox,
                    modifier = Modifier.weight(1f),
                    onClick = { onStockIn(item) }
                )
                AppButton(
                    text = "Stock Out",
                    icon = Icons.Default.LocalShipping,
                    type = com.saifee.inventorysalesmanagement.core.enums.ButtonType.SECONDARY,
                    modifier = Modifier.weight(1f),
                    onClick = { onStockOut(item) }
                )
            }
        }
    }
}

@Composable
private fun HeroProductCard(
    item: ItemEntity,
    categoryName: String,
    stockStatus: StockStatus
) {
    val cardShape = RoundedCornerShape(AppTokens.CornerRadius.Card)
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTokens.Elevation.None),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = AppTokens.Dimensions.BorderWidth, color = borderColor, shape = cardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTokens.Spacing.Medium)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = item.itemName,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(
                            label = categoryName,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StockStatusBadge(status = stockStatus)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = borderColor)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoTag(label = "SKU", value = item.sku ?: "N/A")
                InfoTag(label = "Barcode", value = item.barcode ?: "N/A", icon = Icons.Default.QrCode)
                InfoTag(label = "Unit", value = item.unit.orEmpty().ifEmpty { "pcs" }.uppercase())
            }
        }
    }
}

@Composable
private fun StockThresholdCard(
    item: ItemEntity,
    stockStatus: StockStatus
) {
    val cardShape = RoundedCornerShape(AppTokens.CornerRadius.Card)
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val maxProgress = (item.lowStockLevel * 2).takeIf { it > 0 } ?: 100.0
    val progress = (item.stockQty / maxProgress).toFloat().coerceIn(0f, 1f)

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTokens.Elevation.None),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = AppTokens.Dimensions.BorderWidth, color = borderColor, shape = cardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTokens.Spacing.Medium)
        ) {
            Text(
                text = "Inventory Levels",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Available Quantity",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.stockQty.toInt()} ${item.unit.orEmpty().ifEmpty { "pcs" }}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Min Reorder Threshold: ${item.lowStockLevel.toInt()} ${item.unit.orEmpty().ifEmpty { "pcs" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = when (stockStatus) {
                    StockStatus.IN_STOCK -> MaterialTheme.colorScheme.primary
                    StockStatus.LOW_STOCK -> MaterialTheme.colorScheme.tertiaryContainer
                    StockStatus.OUT_OF_STOCK -> MaterialTheme.colorScheme.error
                },
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        }
    }
}

@Composable
private fun PricingBreakdownCard(item: ItemEntity) {
    val cardShape = RoundedCornerShape(AppTokens.CornerRadius.Card)
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val profit = (item.sellingPrice - item.purchasePrice).coerceAtLeast(0.0)

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTokens.Elevation.None),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = AppTokens.Dimensions.BorderWidth, color = borderColor, shape = cardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTokens.Spacing.Medium)
        ) {
            Text(
                text = "Pricing Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Purchase Price", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$${String.format(Locale.getDefault(), "%.2f", item.purchasePrice)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Column {
                    Text("Selling Price", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$${String.format(Locale.getDefault(), "%.2f", item.sellingPrice)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text("Est. Margin", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+$${String.format(Locale.getDefault(), "%.2f", profit)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun WarehouseAllocationCard(item: ItemEntity) {
    val cardShape = RoundedCornerShape(AppTokens.CornerRadius.Card)
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Card(
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTokens.Elevation.None),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = AppTokens.Dimensions.BorderWidth, color = borderColor, shape = cardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTokens.Spacing.Medium)
        ) {
            Text(
                text = "Warehouse Stock Allocation",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Main Warehouse", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text("${item.stockQty.toInt()} ${item.unit.orEmpty().ifEmpty { "pcs" }}", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun InfoTag(
    label: String,
    value: String,
    icon: ImageVector? = null
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
