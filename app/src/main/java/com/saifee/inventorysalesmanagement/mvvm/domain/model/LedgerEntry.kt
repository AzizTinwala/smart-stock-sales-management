package com.saifee.inventorysalesmanagement.mvvm.domain.model

enum class LedgerTransactionType(val label: String) {
    OPENING_BALANCE("Opening Balance"),
    SALE("Sales Invoice"),
    PURCHASE("Purchase Order"),
    PAYMENT_RECEIVED("Payment Received"),
    PAYMENT_OUT("Payment Made"),
    SALES_RETURN("Sales Return")
}

data class LedgerEntry(
    val transactionId: String,
    val date: Long,
    val type: LedgerTransactionType,
    val referenceNumber: String,
    val description: String?,
    val debitAmount: Double,  // Increases Customer Due / Decreases Vendor Payable
    val creditAmount: Double, // Decreases Customer Due / Increases Vendor Payable
    val runningBalance: Double
)
