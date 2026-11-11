package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.AddItemUiState

@Composable
fun ItemImageSection(
    state: AddItemUiState,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {

    SectionCard {

        SectionHeader(
            title = "Item Image",
            icon = Icons.Outlined.Image,
            background = Color(0xFFE8F3FF),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Large))

        ImagePickerCard(
            imageModel = state.imageUri ?: state.tempImagePath,
            onPickImage = onPickImage,
            onRemoveImage = onRemoveImage
        )

    }

}