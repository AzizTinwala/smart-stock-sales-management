package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ItemGroupFab(

    onClick: () -> Unit

) {

    ExtendedFloatingActionButton(

        onClick = onClick,

        icon = {

            Icon(
                Icons.Rounded.Add,
                contentDescription = null
            )

        },

        text = {

            Text("New Group")

        }

    )

}