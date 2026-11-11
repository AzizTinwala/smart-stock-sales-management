package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.relational.PurchaseWithItems
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetPurchaseByIdUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<String, PurchaseWithItems?>(dispatcher) {
    override suspend fun execute(params: String): PurchaseWithItems? {
        return repo.getPurchaseWithItems(params)
    }
}
