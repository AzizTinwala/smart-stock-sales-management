package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.mvvm.presentation.navigation.AppDestinations

data class QuickModuleItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Enterprise "More" Bottom Sheet providing quick access to secondary StockPilot modules.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockPilotMoreBottomSheet(
    onDismissRequest: () -> Unit,
    onNavigateTo: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val quickModules = listOf(
        QuickModuleItem("Stock In", "Receive Inventory", Icons.Default.AddBox, AppDestinations.PURCHASE.route),
        QuickModuleItem("Stock Out", "Issue Inventory", Icons.Default.LocalShipping, AppDestinations.SALES.route),
        QuickModuleItem("Suppliers", "Manage Vendors", Icons.Default.Groups, AppDestinations.VENDORS.route),
        QuickModuleItem("Warehouses", "Locations & Stock", Icons.Default.Warehouse, AppDestinations.INVENTORY.route),
        QuickModuleItem("Reports", "Analytics & Stats", Icons.Default.Assessment, AppDestinations.REPORTS.route),
        QuickModuleItem("Users & Roles", "Team Access", Icons.Default.Group, AppDestinations.USER_MANAGEMENT.route),
        QuickModuleItem("Sync & Backup", "Cloud Sync", Icons.Default.CloudSync, AppDestinations.SYNC.route),
        QuickModuleItem("Settings", "App Config", Icons.Default.Settings, AppDestinations.SETTINGS.route)
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
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
                        text = "StockPilot Modules",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Quick access to all secondary workflows",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                items(quickModules) { module ->
                    ModuleGridCard(
                        item = module,
                        onClick = {
                            onDismissRequest()
                            onNavigateTo(module.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleGridCard(
    item: QuickModuleItem,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(AppTokens.CornerRadius.Card)
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = cardShape)
            .border(width = AppTokens.Dimensions.BorderWidth, color = borderColor, shape = cardShape)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
