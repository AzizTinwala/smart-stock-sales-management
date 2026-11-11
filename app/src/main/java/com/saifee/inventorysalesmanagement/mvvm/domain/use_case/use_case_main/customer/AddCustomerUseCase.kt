package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddCustomerUseCase @Inject constructor(
    private val repo: TradingRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AddCustomerUseCase.AddCustomerParams, Unit>(dispatcher) {
    override suspend fun execute(params: AddCustomerParams) {
        val user = authRepository.getCurrentUserProfile()
        val targetCompanyId = params.customer.companyId.ifEmpty { user?.companyId ?: "" }
        if (!authorizationManager.canCreateData(user, targetCompanyId)) {
            throw SecurityException("Unauthorized: Cannot create customer records.")
        }
        repo.addCustomer(params.customer)
    }

    data class AddCustomerParams(val customer: CustomerEntity)
}