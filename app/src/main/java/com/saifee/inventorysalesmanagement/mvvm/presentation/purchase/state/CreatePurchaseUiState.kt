package com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.state

import com.saifee.inventorysalesmanagement.core.enums.TaxType
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity

data class PurchaseCartItem(
    val item: ItemEntity,
    val quantity: Double,
    val costPrice: Double
) {
    val total: Double get() = costPrice * quantity
}

data class CreatePurchaseUiState(
    val isLoading: Boolean = false,
    val vendors: List<VendorEntity> = emptyList(),
    val items: List<ItemEntity> = emptyList(),
    
    val selectedVendor: VendorEntity? = null,
    val isCashPurchase: Boolean = false,
    val cart: List<PurchaseCartItem> = emptyList(),
    
    val isSaving: Boolean = false,
    val error: String? = null,

    // Quick Add Vendor
    val showAddVendorDialog: Boolean = false,
    val vendorName: String = "",
    val vendorPhone: String = "",
    val vendorEmail: String = "",
    val vendorAddress: String = "",
    val billingAddress: String = "",
    val state: String = "",
    val openingBalance: String = "",
    val gstNumber: String = "",
    val taxType: TaxType = TaxType.NONE,
    val isSavingVendor: Boolean = false,
    val vendorError: String? = null
) {
    val finalAmount: Double get() = cart.sumOf { it.total }
}
