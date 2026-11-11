package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.dashboard

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.model.DashboardStats
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ActivityRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ActivityLogEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentReceivedEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentsOutEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val itemRepo: ItemRepository,
    private val tradingRepo: TradingRepository,
    private val activityRepo: ActivityRepository,
    private val tenantRepo: TenantRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, Flow<DashboardStats>>(dispatcher) {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override suspend fun execute(params: Unit): Flow<DashboardStats> {
        return tenantRepo.selectedCompanyIdFlow.flatMapLatest { companyId ->
            if (companyId == null) return@flatMapLatest flowOf(DashboardStats())
            
            combine(
                tradingRepo.getSalesByCompany(companyId),
                tradingRepo.getSalesItemsByCompany(companyId),
                tradingRepo.getCustomers(),
                itemRepo.getItems(),
                activityRepo.getLogs(),
                tradingRepo.getPaymentsReceived(),
                tradingRepo.getPaymentsOut()
            ) { array ->
                val sales = array[0] as List<SalesEntity>
                val salesItems = array[1] as List<SalesItemEntity>
                val customers = array[2] as List<CustomerEntity>
                val items = array[3] as List<ItemEntity>
                val logs = array[4] as List<ActivityLogEntity>
                val paymentsIn = array[5] as List<PaymentReceivedEntity>
                val paymentsOut = array[6] as List<PaymentsOutEntity>
                
                try { Log.d("DashboardStats", "Processing ${sales.size} sales for company $companyId") } catch (_: Throwable) {}
                
                // Weekly Sales for Chart
                val weeklySales = mutableListOf<Double>()
                val weeklyLabels = mutableListOf<String>()
                val sdf = SimpleDateFormat("EEE", Locale.getDefault())

                for (i in 6 downTo 0) {
                    val date = Calendar.getInstance()
                    date.add(Calendar.DAY_OF_YEAR, -i)
                    
                    val startOfDay = date.apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis

                    val endOfDay = date.apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }.timeInMillis

                    val dailyTotal = sales
                        .filter { it.createdAt in startOfDay..endOfDay && !it.isDeleted }
                        .sumOf { it.finalAmount }
                    
                    try { Log.d("DashboardStats", "Day -$i: Range $startOfDay - $endOfDay, Found ${sales.filter { it.createdAt in startOfDay..endOfDay }.size} sales, Total: $dailyTotal") } catch (_: Throwable) {}
                    
                    weeklySales.add(dailyTotal)
                    weeklyLabels.add(sdf.format(date.time))
                }

                // Sales Trend Calculation
                val now = System.currentTimeMillis()
                val oneDay = 24 * 60 * 60 * 1000L
                val currentWeekStart = now - (7 * oneDay)
                val prevWeekStart = now - (14 * oneDay)

                val currentWeekSales = sales.filter { it.createdAt >= currentWeekStart && !it.isDeleted }.sumOf { it.finalAmount }
                val prevWeekSales = sales.filter { it.createdAt in prevWeekStart until currentWeekStart && !it.isDeleted }.sumOf { it.finalAmount }

                val trendValue = if (prevWeekSales > 0) {
                    ((currentWeekSales - prevWeekSales) / prevWeekSales * 100).toInt()
                } else if (currentWeekSales > 0) {
                    100
                } else {
                    0
                }

                // Customer Trend
                val currentWeekCustomers = customers.filter { it.createdAt >= currentWeekStart && !it.isDeleted }.size
                val prevWeekCustomers = customers.filter { it.createdAt in prevWeekStart until currentWeekStart && !it.isDeleted }.size
                val cTrendValue = if (prevWeekCustomers > 0) ((currentWeekCustomers - prevWeekCustomers).toDouble() / prevWeekCustomers * 100).toInt() else if (currentWeekCustomers > 0) 100 else 0

                // Inventory Trend (based on new items)
                val currentWeekItems = items.filter { it.createdAt >= currentWeekStart && !it.isDeleted }.size
                val prevWeekItems = items.filter { it.createdAt in prevWeekStart until currentWeekStart && !it.isDeleted }.size
                val iTrendValue = if (prevWeekItems > 0) ((currentWeekItems - prevWeekItems).toDouble() / prevWeekItems * 100).toInt() else if (currentWeekItems > 0) 100 else 0

                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val todaySales = sales.filter { it.createdAt >= todayStart && !it.isDeleted }
                val todayRevenue = todaySales.sumOf { it.finalAmount }
                val totalSalesValue = sales.filter { !it.isDeleted }.sumOf { it.finalAmount }
                
                val totalPaymentsReceived = paymentsIn.sumOf { it.amount }
                val pendingPayments = totalSalesValue - totalPaymentsReceived

                val activeSalesIds = sales.filter { !it.isDeleted }.map { it.salesId }.toSet()
                val activeSalesItems = salesItems.filter { it.salesId in activeSalesIds }
                
                val costOfSoldItems = activeSalesItems.sumOf { saleItem ->
                    val item = items.find { it.itemId == saleItem.itemId }
                    (item?.purchasePrice ?: 0.0) * saleItem.quantity
                }
                val grossProfit = totalSalesValue - costOfSoldItems

                try {
                    Log.d("DashboardStats", "Total Sales Count: ${sales.size}, Non-deleted: ${sales.filter { !it.isDeleted }.size}")
                    Log.d("DashboardStats", "Today Revenue: $todayRevenue from ${todaySales.size} sales. Total Sales: $totalSalesValue")
                } catch (_: Throwable) {}

                DashboardStats(
                    totalSales = totalSalesValue,
                    todayRevenue = todayRevenue,
                    salesCount = sales.filter { !it.isDeleted }.size,
                    customerCount = customers.filter { !it.isDeleted }.size,
                    inventoryCount = items.filter { !it.isDeleted }.sumOf { it.stockQty },
                    pendingPayments = pendingPayments,
                    grossProfit = grossProfit,
                    weeklySales = weeklySales,
                    weeklyLabels = weeklyLabels,
                    recentActivities = logs.take(10),
                    salesTrend = "${if (trendValue >= 0) "+" else ""}$trendValue%",
                    isSalesPositive = trendValue >= 0,
                    customerTrend = "${if (cTrendValue >= 0) "+" else ""}$cTrendValue%",
                    isCustomerPositive = cTrendValue >= 0,
                    inventoryTrend = "${if (iTrendValue >= 0) "+" else ""}$iTrendValue%",
                    isInventoryPositive = iTrendValue >= 0
                )
            }
        }
    }
}
