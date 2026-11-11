package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetCustomerByIdUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<String, CustomerEntity?>(dispatcher) {
    override suspend fun execute(params: String): CustomerEntity? {
        return repo.getCustomerById(params)
    }
}
