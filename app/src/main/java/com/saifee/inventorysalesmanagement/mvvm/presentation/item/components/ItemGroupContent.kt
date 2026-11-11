package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.state.ItemGroupUiState
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.viewmodel.ItemGroupViewModel


@Composable
fun ItemGroupContent(

    viewModel: ItemGroupViewModel,
    state: ItemGroupUiState

) {

    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 12.dp,
            bottom = 100.dp
        ),

        verticalArrangement = Arrangement.spacedBy(18.dp)

    ) {

        item {

            HeaderSection()

        }
        item {

            InventorySearchBar(

                value = state.search,

                onValueChange = viewModel::onSearchChange

            )

        }
        if (state.itemGroups.isEmpty()) {

            item {

                EmptyState()

            }

        }
        items(

            items = state.itemGroups,

            key = { it.groupId }

        ) { group ->
            AnimatedVisibility(

                visible = true,

                enter = fadeIn() + expandVertically()

            ) {
                SwipeToDismissBox(

                    state = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart) {
                                viewModel.deleteGroup(group)
                                true
                            } else false
                        }
                    ),

                    backgroundContent = {

                        Box(

                            Modifier.fillMaxSize(),

                            contentAlignment = Alignment.CenterEnd

                        ) {

                            Icon(

                                Icons.Default.Delete,

                                null

                            )

                        }

                    }

                ) {
                    ItemGroupCard(
                        itemGroup = group,
                        onClick = {},
                        onEdit = { viewModel.setShowAddEditDialog(true, group) },
                        onViewItems = { },
                        onDelete = { viewModel.deleteGroup(group) }
                    )
                }

            }

        }

    }

}