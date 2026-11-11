package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.saifee.inventorysalesmanagement.core.sealed_classes.InputState

@Composable
fun AppNumericField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    state: InputState = InputState.Normal,
    message: String? = null,
    prefixText: String? = null,
    suffixText: String? = null,
    enabled: Boolean = true
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        state = state,
        message = message,
        enabled = enabled,
        singleLine = true,
        prefix = prefixText?.let { { AppText(it) } },
        suffix = suffixText?.let { { AppText(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}
