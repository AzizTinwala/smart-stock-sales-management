package com.saifee.inventorysalesmanagement.mvvm.presentation.sales.state

import com.saifee.inventorysalesmanagement.core.enums.TaxType
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity

data class CartItem(
    val item: ItemEntity,
    val quantity: Double,
    val price: Double = item.sellingPrice
) {
    val total: Double get() = price * quantity
}

data class CreateSaleUiState(
    val isLoading: Boolean = false,
    val customers: List<CustomerEntity> = emptyList(),
    val items: List<ItemEntity> = emptyList(),
    
    val selectedCustomer: CustomerEntity? = null,
    val isCashSale: Boolean = false,
    val cart: List<CartItem> = emptyList(),
    
    val discount: Double = 0.0,
    val taxType: TaxType = TaxType.INTRA_STATE, // Default to Intra-state
    val taxRate: Double = 0.18, // Default 18%
    
    val isRoundOffEnabled: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,

    // Quick Add Customer
    val showAddCustomerDialog: Boolean = false,
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val customerAddress: String = "",
    val billingAddress: String = "",
    val shippingAddress: String = "",
    val state: String = "",
    val openingBalance: String = "",
    val gstNumber: String = "",
    val quickTaxType: TaxType = TaxType.NONE,
    val isSavingCustomer: Boolean = false,
    val customerError: String? = null
) {
    val subTotal: Double get() = cart.sumOf { it.total }
    
    val cgst: Double get() = if (taxType == TaxType.INTRA_STATE) (subTotal * (taxRate / 2)) else 0.0
    val sgst: Double get() = if (taxType == TaxType.INTRA_STATE) (subTotal * (taxRate / 2)) else 0.0
    val igst: Double get() = if (taxType == TaxType.INTER_STATE) (subTotal * taxRate) else 0.0
    
    val taxAmount: Double get() = if (taxType == TaxType.INTRA_STATE) (cgst + sgst) else igst
    
    val rawFinalAmount: Double get() = (subTotal + taxAmount) - discount
    
    val finalAmount: Double get() = if (isRoundOffEnabled) kotlin.math.round(rawFinalAmount) else rawFinalAmount
    val roundOffAmount: Double get() = finalAmount - rawFinalAmount
}
