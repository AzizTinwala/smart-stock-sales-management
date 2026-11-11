package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AdjustStockUseCase @Inject constructor(
    private val repo: ItemRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AdjustStockUseCase.AdjustStockParams, Unit>(dispatcher) {

    override suspend fun execute(params: AdjustStockParams) {
        repo.adjustStock(params.itemId, params.quantity, params.reason, params.remark)
    }

    data class AdjustStockParams(
        val itemId: Long,
        val quantity: Double,
        val reason: String,
        val remark: String = ""
    )
}
