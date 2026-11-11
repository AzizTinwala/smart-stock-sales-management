package com.saifee.inventorysalesmanagement.core.ui.system

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

object AppDefaults {

    val textColor @Composable
    get() = MaterialTheme.colorScheme.onBackground

    val primaryColor @Composable
    get() = MaterialTheme.colorScheme.primary

    val shapeRounded
        @Composable get() = RoundedCornerShape(Dimens.cornerRadius)
}