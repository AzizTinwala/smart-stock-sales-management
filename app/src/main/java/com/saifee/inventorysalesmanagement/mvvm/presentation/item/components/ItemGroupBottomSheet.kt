package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.AppTextStyle
import com.saifee.inventorysalesmanagement.core.sealed_classes.InputState
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppFormColumn
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemGroupBottomSheet(
    isEditMode: Boolean = false,
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    groupDescription: String = "",
    onGroupDescriptionChange: (String) -> Unit = {},
    groupNameError: String? = null,
    isSaving: Boolean = false,
    errorMessage: String? = null,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            AppText(
                text = if (isEditMode) "Edit Group" else "Add New Group",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AppFormColumn {
                AppTextField(
                    label = "Group Name",
                    value = groupName,
                    placeholder = "e.g., Electronics",
                    onValueChange = onGroupNameChange,
                    state = if (groupNameError != null) InputState.Error else InputState.Normal,
                    message = groupNameError,
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    label = "Description (Optional)",
                    value = groupDescription,
                    placeholder = "Enter group details",
                    onValueChange = onGroupDescriptionChange,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    AppText(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = AppTextStyle.LABEL
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppButton(
                    text = if (isEditMode) "Update Group" else "Save Group",
                    onClick = onSave,
                    isLoading = isSaving,
                    enabled = groupName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
