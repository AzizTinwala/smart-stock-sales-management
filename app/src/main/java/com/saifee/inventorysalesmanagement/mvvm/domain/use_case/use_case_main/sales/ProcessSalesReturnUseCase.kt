package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnItemEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ProcessSalesReturnUseCase @Inject constructor(
    private val tradingRepository: TradingRepository,
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<ProcessSalesReturnUseCase.ProcessReturnParam, Unit>(dispatcher) {

    override suspend fun execute(params: ProcessReturnParam) {
        val user = authRepository.getCurrentUserProfile()
        val targetCompanyId = params.salesReturn.companyId.ifEmpty { user?.companyId ?: "" }
        if (!authorizationManager.canCreateData(user, targetCompanyId)) {
            throw SecurityException("Unauthorized: Cannot process sales returns.")
        }

        for (item in params.returnItems) {
            if (item.itemCondition == "SELLABLE") {
                itemRepository.adjustStockDelta(
                    itemId = item.itemId,
                    quantityDelta = item.quantity,
                    reason = "Sales Return (Restock)",
                    remark = "Return ID: ${params.salesReturn.salesReturnId}"
                )
            } else {
                itemRepository.adjustStockDelta(
                    itemId = item.itemId,
                    quantityDelta = 0.0,
                    reason = "Sales Return (Damaged/Quarantine)",
                    remark = "Returned as damaged. Qty: ${item.quantity}"
                )
            }
        }

        tradingRepository.addSalesReturn(params.salesReturn, params.returnItems)
    }

    data class ProcessReturnParam(
        val salesReturn: SalesReturnEntity,
        val returnItems: List<SalesReturnItemEntity>
    )
}
