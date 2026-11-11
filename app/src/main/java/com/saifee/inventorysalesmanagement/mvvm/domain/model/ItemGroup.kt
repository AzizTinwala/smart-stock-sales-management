package com.saifee.inventorysalesmanagement.mvvm.domain.model


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector

data class ItemGroup(

    val id: Long,

    val name: String,

    val totalSku: Int,

    val lowStock: Int,

    val priority: Int,

    val icon: ImageVector = Icons.Outlined.Inventory2

)