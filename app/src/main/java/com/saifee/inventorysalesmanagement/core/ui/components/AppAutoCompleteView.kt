package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.sealed_classes.InputState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppAutoCompleteTextView(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: String,
    items: List<T>,
    itemToString: (T) -> String = { it.toString() },
    onItemSelected: (T) -> Unit,
    placeholder: String? = null,
    state: InputState = InputState.Normal,
    message: String? = null,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredItems = items.filter { itemToString(it).contains(value, ignoreCase = true) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = value,
                onValueChange = {},
                readOnly = true,
                label = label,
                placeholder = placeholder,
                state = state,
                enabled = enabled,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            if (filteredItems.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filteredItems.forEach { item ->
                        DropdownMenuItem(
                            text = { AppText(itemToString(item)) },
                            onClick = {
                                onItemSelected(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        
        if (message != null) {
            Spacer(modifier = Modifier.height(AppTokens.Spacing.ExtraSmall))
            AppText(
                text = message,
                style = MaterialTheme.typography.labelSmall,
                color = when (state) {
                    is InputState.Error -> MaterialTheme.colorScheme.error
                    is InputState.Success -> Color(0xFF4CAF50)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(horizontal = AppTokens.Spacing.Medium)
            )
        }
    }
}
