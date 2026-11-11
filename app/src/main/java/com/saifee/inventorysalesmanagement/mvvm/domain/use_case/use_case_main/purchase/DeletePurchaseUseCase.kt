package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeletePurchaseUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<DeletePurchaseUseCase.DeletePurchaseParams, Unit>(dispatcher) {
    override suspend fun execute(params: DeletePurchaseParams) {
        repo.deletePurchase(params.purchaseId)
    }

    data class DeletePurchaseParams(val purchaseId: String)
}
