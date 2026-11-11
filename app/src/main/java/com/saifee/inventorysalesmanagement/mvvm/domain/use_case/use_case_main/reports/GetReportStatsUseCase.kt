package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.reports

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.model.ReportStats
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class GetReportStatsUseCase @Inject constructor(
    private val itemRepo: ItemRepository,
    private val tradingRepo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, Flow<ReportStats>>(dispatcher) {

    override suspend fun execute(params: Unit): Flow<ReportStats> {
        return combine(
            tradingRepo.getSales(),
            itemRepo.getItems(),
            tradingRepo.getPurchases()
        ) { sales, items, purchases ->
            
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            val thisMonthSales = sales.filter {
                val sCal = Calendar.getInstance().apply { timeInMillis = it.createdAt }
                sCal.get(Calendar.MONTH) == currentMonth && sCal.get(Calendar.YEAR) == currentYear
            }

            val totalSalesActual = thisMonthSales.sumOf { it.finalAmount }
            val stockValue = items.sumOf { it.stockQty * it.purchasePrice }
            val purchaseCost = purchases.sumOf { it.totalAmount }
            
            // Simplified Turnover: Total Sales / Avg Inventory (here just current inventory)
            val turnover = if (stockValue > 0) totalSalesActual / stockValue else 0.0

            ReportStats(
                monthlySalesActual = totalSalesActual,
                monthlySalesGoal = 20000.0, // Default goal
                totalStockValue = stockValue,
                totalPurchaseCost = purchaseCost,
                totalItemsSold = thisMonthSales.size,
                inventoryTurnover = turnover
            )
        }
    }
}
