package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.mvvm.presentation.navigation.AppDestinations

private data class BottomNavItem(
    val route: String?,
    val label: String,
    val icon: ImageVector,
    val isMore: Boolean = false
)

/**
 * StockPilot Hybrid Bottom Navigation Bar (5 Primary Destinations)
 */
@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigateTo: (String) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(AppDestinations.DASHBOARD.route, "Dashboard", Icons.Default.Dashboard),
        BottomNavItem(AppDestinations.ITEMS.route, "Products", Icons.Default.Inventory2),
        BottomNavItem(AppDestinations.INVENTORY.route, "Transactions", Icons.AutoMirrored.Filled.ReceiptLong),
        BottomNavItem(AppDestinations.ITEM_GROUP.route, "Categories", Icons.Default.Category),
        BottomNavItem(null, "More", Icons.Default.MoreHoriz, isMore = true)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier
    ) {
        items.forEach { item ->
            val isSelected = !item.isMore && currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item.isMore) {
                        onMoreClick()
                    } else if (item.route != null && !isSelected) {
                        onNavigateTo(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
