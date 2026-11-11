package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppAutoCompleteTextView
import com.saifee.inventorysalesmanagement.core.ui.components.AppSwitch
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.AddItemUiState

@Composable
fun UnitTrackingSection(
    state: AddItemUiState,
    onUnitSelected: (String) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onTrackInventoryChange: (Boolean) -> Unit,
    onLowStockChange: (String) -> Unit,
    onCriticalStockChange: (String) -> Unit,
    onScanBarcode: () -> Unit,
    onGenerateBarcode: () -> Unit
) {

    val units = listOf(
        "Pieces",
        "Box",
        "Packet",
        "Kilogram",
        "Gram",
        "Liter",
        "Meter",
        "Feet"
    )

    SectionCard {

        SectionHeader(
            title = "Unit & Tracking",
            icon = Icons.Outlined.Inventory,
            background = Color(0xFFE8F8EC),
            tint = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

        AppAutoCompleteTextView(
            label = "UNIT",
            value = state.unit,
            items = units,
            onItemSelected = onUnitSelected
        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

        BarcodeField(
            value = state.barcode,
            onValueChange = onBarcodeChange,
            onScanClick = onScanBarcode,
            onGenerateClick = onGenerateBarcode
        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

        AppSwitch(
            title = "Track Inventory",
            subtitle = "Enable stock management",
            checked = state.trackInventory,
            onCheckedChange = onTrackInventoryChange
        )

        if (state.trackInventory) {
            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
            ) {
                AppTextField(
                    modifier = Modifier.weight(1f),
                    label = "LOW STOCK",
                    value = state.lowStockValue,
                    placeholder = "0",
                    onValueChange = onLowStockChange
                )

                AppTextField(
                    modifier = Modifier.weight(1f),
                    label = "CRITICAL STOCK",
                    value = state.criticalStockValue,
                    placeholder = "0",
                    onValueChange = onCriticalStockChange
                )
            }
        }

    }

}