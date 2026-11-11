package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.reports

import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PurchaseBatchDao
import javax.inject.Inject
import javax.inject.Singleton

data class FifoAllocationResult(
    val totalCogs: Double,
    val batchUpdates: List<Pair<Long, Double>> // batchId to new quantityRemaining
)

@Singleton
class FifoCostCalculator @Inject constructor(
    private val purchaseBatchDao: PurchaseBatchDao
) {
    suspend fun calculateCogsAndAllocate(
        companyId: String,
        locationId: String,
        itemId: Long,
        quantitySold: Double,
        defaultFallbackCost: Double
    ): FifoAllocationResult {
        val batches = purchaseBatchDao.getAvailableBatchesForItem(itemId, companyId, locationId)
        var remainingQtyToAllocate = quantitySold
        var totalCogs = 0.0
        val updates = mutableListOf<Pair<Long, Double>>()

        for (batch in batches) {
            if (remainingQtyToAllocate <= 0.0) break

            val qtyFromBatch = minOf(remainingQtyToAllocate, batch.quantityRemaining)
            totalCogs += qtyFromBatch * batch.unitCost
            val newRemaining = batch.quantityRemaining - qtyFromBatch
            updates.add(batch.batchId to newRemaining)
            remainingQtyToAllocate -= qtyFromBatch
        }

        if (remainingQtyToAllocate > 0.0) {
            totalCogs += remainingQtyToAllocate * defaultFallbackCost
        }

        return FifoAllocationResult(totalCogs, updates)
    }
}
