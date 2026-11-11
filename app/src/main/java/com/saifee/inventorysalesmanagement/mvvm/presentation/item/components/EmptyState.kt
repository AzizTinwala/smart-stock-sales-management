package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun EmptyState() {

    Column(

        modifier = Modifier.fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Icon(

            Icons.Outlined.Inventory2,

            null,

            modifier = Modifier.size(80.dp)

        )

        Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

        AppText(

            "No Item Groups Found",

            style = MaterialTheme.typography.headlineSmall

        )

        AppText(

            "Tap + to create one."

        )

    }

}
