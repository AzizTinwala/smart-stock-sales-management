package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens

@Composable
fun BarcodeField(
    value: String,
    onValueChange: (String) -> Unit,
    onScanClick: () -> Unit,
    onGenerateClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTokens.Spacing.ExtraSmall)
    ) {
        AppTextField(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = onValueChange,
            label = "Barcode / SKU",
            placeholder = "Scan or enter code",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.QrCode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onScanClick) {
                    Icon(
                        imageVector = Icons.Rounded.QrCodeScanner,
                        contentDescription = "Scan Barcode",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            shape = RoundedCornerShape(20.dp)
        )

        IconButton(
            onClick = onGenerateClick,
            modifier = Modifier.padding(top = 16.dp) // Offset for the label in AppTextField
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoFixHigh,
                contentDescription = "Generate Barcode",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
