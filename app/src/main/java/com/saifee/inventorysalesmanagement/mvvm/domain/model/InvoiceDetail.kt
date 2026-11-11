package com.saifee.inventorysalesmanagement.mvvm.domain.model

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity

data class InvoiceDetail(
    val sale: SalesEntity,
    val items: List<InvoiceItem>,
    val customer: CustomerEntity?,
    val company: CompanyEntity
)

data class InvoiceItem(
    val itemName: String,
    val sku: String?,
    val quantity: Double,
    val price: Double,
    val total: Double,
    val gstPercentage: Double
)
