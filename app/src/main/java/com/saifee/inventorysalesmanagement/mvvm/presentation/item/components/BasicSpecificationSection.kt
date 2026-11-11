package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppAutoCompleteTextView
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.AddItemUiState

@Composable
fun BasicSpecificationSection(
    state: AddItemUiState,
    onItemNameChange: (String) -> Unit,
    onGroupSelected: (String) -> Unit,
    onAddGroupClick: () -> Unit
) {

    SectionCard {

        SectionHeader(
            title = "Basic Specifications",
            icon = Icons.Outlined.Inventory2,
            background = Color(0xFFD9ECFF),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

        AppTextField(
            label = "ITEM NAME",
            value = state.itemName,
            placeholder = "e.g., Industrial Steel Beam",
            onValueChange = onItemNameChange,
            state = state.itemNameInputState,
            message = state.itemNameError
        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppAutoCompleteTextView(
                modifier = Modifier.weight(1f),
                label = "ITEM GROUP",
                value = state.itemGroup,
                items = state.availableGroups,
                state = state.itemGroupInputState,
                message = state.itemGroupError,
                onItemSelected = onGroupSelected
              )

            IconButton(onClick = onAddGroupClick) {
                Icon(Icons.Default.Add, contentDescription = "Add New Group")
            }
        }

    }
}