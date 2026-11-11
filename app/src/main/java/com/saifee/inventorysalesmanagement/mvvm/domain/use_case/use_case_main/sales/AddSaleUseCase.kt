package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddSaleUseCase @Inject constructor(
    private val repo: TradingRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AddSaleUseCase.AddSalesParam, Unit>(dispatcher) {
    override suspend fun execute(params: AddSalesParam) {
        val user = authRepository.getCurrentUserProfile()
        val targetCompanyId = params.sale.companyId.ifEmpty { user?.companyId ?: "" }
        if (!authorizationManager.canCreateData(user, targetCompanyId)) {
            throw SecurityException("Unauthorized: Cannot create sale records.")
        }
        repo.addSales(params.sale, params.salesItems)
    }

    data class AddSalesParam(val sale: SalesEntity, val salesItems: List<SalesItemEntity>)
}