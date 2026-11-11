package com.saifee.inventorysalesmanagement.mvvm.domain.model

data class ReportStats(
    val monthlySalesActual: Double = 0.0,
    val monthlySalesGoal: Double = 10000.0,
    val totalStockValue: Double = 0.0,
    val totalPurchaseCost: Double = 0.0,
    val totalItemsSold: Int = 0,
    val inventoryTurnover: Double = 0.0
) {
    val salesProgress: Float 
        get() = if (monthlySalesGoal > 0) (monthlySalesActual / monthlySalesGoal).toFloat().coerceIn(0f, 1f) else 0f
}
