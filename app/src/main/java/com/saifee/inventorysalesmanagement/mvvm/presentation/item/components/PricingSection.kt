package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.AddItemUiState

@Composable
fun PricingSection(
    state: AddItemUiState,
    onCostPriceChange: (String) -> Unit,
    onSellPriceChange: (String) -> Unit,
    onGstPercentageChange: (String) -> Unit,
    onHsnNumberChange: (String) -> Unit
) {

    SectionCard {

        SectionHeader(
            title = "Pricing & Tax",
            icon = Icons.Outlined.Payments,
            background = Color(0xFFFFE8DC),
            tint = Color(0xFF8A4B08)
        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
        ) {

            AppTextField(
                modifier = Modifier.weight(1f),
                label = "Cost Price (Excl. GST)",
                value = state.costPrice,
                placeholder = "0.00",
                onValueChange = onCostPriceChange,
                state = state.costPriceInputState,
                message = state.costPriceError, 
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            AppTextField(
                modifier = Modifier.weight(1f),
                label = "Sell Price (Excl. GST)",
                value = state.sellPrice,
                placeholder = "0.00",
                onValueChange = onSellPriceChange, 
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

        }

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.Medium)
        ) {

            AppTextField(
                modifier = Modifier.weight(1f),
                label = "GST (%)",
                value = state.gstPercentage,
                placeholder = "0",
                onValueChange = onGstPercentageChange, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AppTextField(
                modifier = Modifier.weight(1f),
                label = "HSN Number",
                value = state.hsnNumber,
                placeholder = "Optional",
                onValueChange = onHsnNumberChange
            )

        }

    }

}