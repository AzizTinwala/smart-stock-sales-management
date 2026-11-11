package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventorySearchBar(

    value: String,

    onValueChange: (String) -> Unit

) {

    SearchBar(

        query = value,

        onQueryChange = onValueChange,

        onSearch = {},

        active = false,

        onActiveChange = {},

        placeholder = {

            Text("Search Item Groups")

        },

        leadingIcon = {

            Icon(
                Icons.Default.Search,
                null
            )

        }

    ) {

    }

}