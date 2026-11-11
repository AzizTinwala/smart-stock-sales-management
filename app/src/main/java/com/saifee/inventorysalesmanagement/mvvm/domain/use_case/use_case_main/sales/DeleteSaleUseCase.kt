package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteSaleUseCase @Inject constructor(
    private val repo: TradingRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<DeleteSaleUseCase.DeleteSaleParams, Unit>(dispatcher) {

    override suspend fun execute(params: DeleteSaleParams) {
        val user = authRepository.getCurrentUserProfile()
        if (!authorizationManager.canDeleteData(user, user?.companyId ?: "")) {
            throw SecurityException("Unauthorized: Cannot delete sale records.")
        }
        repo.deleteSales(params.saleId)
    }

    data class DeleteSaleParams(val saleId: String)
}
