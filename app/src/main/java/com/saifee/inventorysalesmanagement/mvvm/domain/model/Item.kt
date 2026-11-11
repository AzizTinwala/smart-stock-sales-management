package com.saifee.inventorysalesmanagement.mvvm.domain.model
data class Item(
    val id: String,
    val name: String,
    val price: Double,
    val stock: Int,
    val barcode: String
)