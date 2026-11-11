package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

import com.saifee.inventorysalesmanagement.mvvm.data.local.relational.SalesWithItems

class GetSaleByIdUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<String, SalesWithItems?>(dispatcher) {
    override suspend fun execute(params: String): SalesWithItems? {
        return repo.getSalesWithItems(params)
    }
}
