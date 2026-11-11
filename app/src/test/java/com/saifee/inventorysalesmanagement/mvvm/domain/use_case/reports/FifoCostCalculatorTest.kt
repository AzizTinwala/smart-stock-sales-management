package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.reports

import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PurchaseBatchDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseBatchEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class FifoCostCalculatorTest {

    @Mock
    private lateinit var purchaseBatchDao: PurchaseBatchDao

    private lateinit var fifoCostCalculator: FifoCostCalculator

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        fifoCostCalculator = FifoCostCalculator(purchaseBatchDao)
    }

    @Test
    fun `calculateCogsAndAllocate consumes batches in FIFO order`() = runTest {
        val companyId = "COMP1"
        val locationId = "LOC1"
        val itemId = 100L

        val batch1 = PurchaseBatchEntity(
            batchId = 1L,
            itemId = itemId,
            purchaseId = 10L,
            companyId = companyId,
            locationId = locationId,
            unitCost = 10.0,
            quantityPurchased = 10.0,
            quantityRemaining = 10.0,
            createdAt = 1000L
        )

        val batch2 = PurchaseBatchEntity(
            batchId = 2L,
            itemId = itemId,
            purchaseId = 11L,
            companyId = companyId,
            locationId = locationId,
            unitCost = 15.0,
            quantityPurchased = 10.0,
            quantityRemaining = 10.0,
            createdAt = 2000L
        )

        `when`(purchaseBatchDao.getAvailableBatchesForItem(itemId, companyId, locationId))
            .thenReturn(listOf(batch1, batch2))

        val result = fifoCostCalculator.calculateCogsAndAllocate(
            companyId = companyId,
            locationId = locationId,
            itemId = itemId,
            quantitySold = 15.0,
            defaultFallbackCost = 20.0
        )

        assertEquals(175.0, result.totalCogs, 0.001)
        assertEquals(2, result.batchUpdates.size)
        assertEquals(1L to 0.0, result.batchUpdates[0])
        assertEquals(2L to 5.0, result.batchUpdates[1])
    }

    @Test
    fun `calculateCogsAndAllocate uses fallback cost when inventory is exhausted`() = runTest {
        val companyId = "COMP1"
        val locationId = "LOC1"
        val itemId = 100L

        val batch1 = PurchaseBatchEntity(
            batchId = 1L,
            itemId = itemId,
            purchaseId = 10L,
            companyId = companyId,
            locationId = locationId,
            unitCost = 10.0,
            quantityPurchased = 5.0,
            quantityRemaining = 5.0,
            createdAt = 1000L
        )

        `when`(purchaseBatchDao.getAvailableBatchesForItem(itemId, companyId, locationId))
            .thenReturn(listOf(batch1))

        val result = fifoCostCalculator.calculateCogsAndAllocate(
            companyId = companyId,
            locationId = locationId,
            itemId = itemId,
            quantitySold = 10.0,
            defaultFallbackCost = 20.0
        )

        assertEquals(150.0, result.totalCogs, 0.001)
        assertEquals(1, result.batchUpdates.size)
        assertEquals(1L to 0.0, result.batchUpdates[0])
    }
}
