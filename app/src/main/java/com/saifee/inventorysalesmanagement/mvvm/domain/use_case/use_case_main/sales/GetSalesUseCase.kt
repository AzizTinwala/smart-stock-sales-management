package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSalesUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, Flow<List<SalesEntity>>>(dispatcher) {
    override suspend fun execute(params: Unit): Flow<List<SalesEntity>> {
        return repo.getSales()
    }
}