package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateCustomerUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<UpdateCustomerUseCase.UpdateCustomerParams, Unit>(dispatcher) {
    override suspend fun execute(params: UpdateCustomerParams) {
        repo.addCustomer(params.customer) // addCustomer handles insert/update in this repo
    }

    data class UpdateCustomerParams(val customer: CustomerEntity)
}
