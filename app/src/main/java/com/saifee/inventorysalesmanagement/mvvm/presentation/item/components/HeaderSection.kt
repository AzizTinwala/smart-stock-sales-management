package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeaderSection() {

    Column {

        ScreenTitle(
            text = "Organizational Ledger"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScreenSubtitle(
            text = "Manage and categorize your inventory assets."
        )

    }

}