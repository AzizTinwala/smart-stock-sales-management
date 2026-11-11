package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.payment

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentsOutEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddPaymentOutUseCase @Inject constructor(
    private val repo: TradingRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AddPaymentOutUseCase.AddPaymentOutParams, Unit>(dispatcher) {

    override suspend fun execute(params: AddPaymentOutParams) {
        val user = authRepository.getCurrentUserProfile()
        val targetCompanyId = params.payment.companyId.ifEmpty { user?.companyId ?: "" }
        if (!authorizationManager.canCreateData(user, targetCompanyId)) {
            throw SecurityException("Unauthorized: Cannot create payment out records.")
        }
        repo.addPaymentOut(params.payment)
    }

    data class AddPaymentOutParams(val payment: PaymentsOutEntity)
}