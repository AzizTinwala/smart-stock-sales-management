package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseItemEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdatePurchaseUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<UpdatePurchaseUseCase.UpdatePurchaseParams, Unit>(dispatcher) {
    override suspend fun execute(params: UpdatePurchaseParams) {
        repo.updatePurchase(params.purchase, params.items)
    }

    data class UpdatePurchaseParams(val purchase: PurchaseEntity, val items: List<PurchaseItemEntity>)
}
