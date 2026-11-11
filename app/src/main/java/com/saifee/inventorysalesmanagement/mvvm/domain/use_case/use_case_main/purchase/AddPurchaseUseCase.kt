package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseItemEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddPurchaseUseCase @Inject constructor(
    private val repo: TradingRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AddPurchaseUseCase.AddPurchaseParams, Unit>(dispatcher) {

    override suspend fun execute(params: AddPurchaseParams) {
        val user = authRepository.getCurrentUserProfile()
        val targetCompanyId = params.purchase.companyId.ifEmpty { user?.companyId ?: "" }
        if (!authorizationManager.canCreateData(user, targetCompanyId)) {
            throw SecurityException("Unauthorized: Cannot create purchase records.")
        }
        repo.addPurchase(params.purchase, params.items)
    }

    data class AddPurchaseParams(
        val purchase: PurchaseEntity,
        val items: List<PurchaseItemEntity>
    )
}