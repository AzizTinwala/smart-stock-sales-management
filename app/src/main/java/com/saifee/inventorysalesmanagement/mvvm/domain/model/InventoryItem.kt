package com.saifee.inventorysalesmanagement.mvvm.domain.model

data class InventoryItem(

    val id: Int,

    val name: String,

    val sku: String,

    val image: Int?,

    val imagePath: String? = null,

    val imageUrl: String? = null,

    val quantity: Int,

    val priority: Boolean = false,

    val lowStock: Boolean = false
)