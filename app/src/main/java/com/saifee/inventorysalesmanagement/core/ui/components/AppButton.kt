package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.ButtonType
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens

/**
 * StockPilot Enterprise Button Component (Spec: 10dp corner radius, 48dp minimum height)
 */
@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String,
    type: ButtonType = ButtonType.PRIMARY,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    onClick: () -> Unit
) {
    val buttonHeight = AppTokens.Dimensions.ButtonHeight
    val shape = RoundedCornerShape(AppTokens.CornerRadius.Button)

    when (type) {
        ButtonType.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !isLoading,
                shape = shape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                contentPadding = contentPadding
            ) {
                ButtonContent(text, icon, isLoading, MaterialTheme.colorScheme.primary)
            }
        }
        ButtonType.DANGER -> {
            Button(
                onClick = onClick,
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                contentPadding = contentPadding
            ) {
                ButtonContent(text, icon, isLoading, MaterialTheme.colorScheme.onError)
            }
        }
        ButtonType.PRIMARY -> {
            Button(
                onClick = onClick,
                enabled = enabled && !isLoading,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                contentPadding = contentPadding
            ) {
                ButtonContent(text, icon, isLoading, MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    isLoading: Boolean,
    contentColor: androidx.compose.ui.graphics.Color
) {
    if (isLoading) {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            color = contentColor,
            modifier = Modifier.size(AppTokens.IconSize.Compact)
        )
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(AppTokens.IconSize.Compact)
                )
                Spacer(Modifier.width(AppTokens.Spacing.Small))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = contentColor
            )
        }
    }
}
