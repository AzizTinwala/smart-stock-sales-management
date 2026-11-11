package com.saifee.inventorysalesmanagement.mvvm.domain.model

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ActivityLogEntity

data class DashboardStats(
    val totalSales: Double = 0.0,
    val todayRevenue: Double = 0.0,
    val salesCount: Int = 0,
    val customerCount: Int = 0,
    val inventoryCount: Double = 1420.0,
    val totalStockValue: Double = 128450.00,
    val lowStockCount: Int = 12,
    val outOfStockCount: Int = 3,
    val pendingPayments: Double = 0.0,
    val grossProfit: Double = 0.0,
    val weeklySales: List<Double> = listOf(4200.0, 5800.0, 6100.0, 4900.0, 7200.0, 8100.0, 6800.0),
    val weeklyLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    val recentActivities: List<ActivityLogEntity> = emptyList(),
    val salesTrend: String = "+12.5%",
    val isSalesPositive: Boolean = true,
    val customerTrend: String = "+4.2%",
    val isCustomerPositive: Boolean = true,
    val inventoryTrend: String = "-1.8%",
    val isInventoryPositive: Boolean = false
)
