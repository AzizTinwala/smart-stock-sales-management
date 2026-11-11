package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.dashboard

import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentReceivedEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ActivityRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetDashboardStatsUseCaseTest {

    @Mock
    private lateinit var itemRepo: ItemRepository

    @Mock
    private lateinit var tradingRepo: TradingRepository

    @Mock
    private lateinit var activityRepo: ActivityRepository

    @Mock
    private lateinit var tenantRepo: TenantRepository

    private lateinit var getDashboardStatsUseCase: GetDashboardStatsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getDashboardStatsUseCase = GetDashboardStatsUseCase(
            itemRepo = itemRepo,
            tradingRepo = tradingRepo,
            activityRepo = activityRepo,
            tenantRepo = tenantRepo,
            dispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun `dashboard stats calculates total revenue, counts and gross profit correctly`() = runTest {
        val companyId = "COMP1"

        val item1 = ItemEntity(itemId = 1L, itemName = "Widget A", purchasePrice = 50.0, stockQty = 100.0)
        val sale1 = SalesEntity(salesId = "S1", companyId = companyId, finalAmount = 200.0, isDeleted = false)
        val saleItem1 = SalesItemEntity(
            salesItemId = "SI1",
            companyId = companyId,
            locationId = "LOC1",
            salesId = "S1",
            itemId = 1L,
            quantity = 2.0,
            price = 100.0,
            total = 200.0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val customer1 = CustomerEntity(customerId = "C1", companyId = companyId)
        val paymentIn1 = PaymentReceivedEntity(paymentReceivedId = "P1", amount = 150.0)

        `when`(tenantRepo.selectedCompanyIdFlow).thenReturn(flowOf(companyId))
        `when`(tradingRepo.getSalesByCompany(companyId)).thenReturn(flowOf(listOf(sale1)))
        `when`(tradingRepo.getSalesItemsByCompany(companyId)).thenReturn(flowOf(listOf(saleItem1)))
        `when`(tradingRepo.getCustomers()).thenReturn(flowOf(listOf(customer1)))
        `when`(itemRepo.getItems()).thenReturn(flowOf(listOf(item1)))
        `when`(activityRepo.getLogs()).thenReturn(flowOf(emptyList()))
        `when`(tradingRepo.getPaymentsReceived()).thenReturn(flowOf(listOf(paymentIn1)))
        `when`(tradingRepo.getPaymentsOut()).thenReturn(flowOf(emptyList()))

        val resultState = getDashboardStatsUseCase(Unit)

        assertTrue(resultState is ResultState.Success)
        val stats = (resultState as ResultState.Success).data.first()

        assertEquals(200.0, stats.totalSales, 0.001)
        assertEquals(1, stats.salesCount)
        assertEquals(1, stats.customerCount)
        assertEquals(100.0, stats.inventoryCount, 0.001)
        assertEquals(50.0, stats.pendingPayments, 0.001) // 200 sales - 150 payments = 50 pending
        assertEquals(100.0, stats.grossProfit, 0.001) // 200 revenue - (50 purchase price * 2 qty) = 100
    }
}
