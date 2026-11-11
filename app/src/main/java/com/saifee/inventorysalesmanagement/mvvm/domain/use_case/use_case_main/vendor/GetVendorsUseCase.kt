package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVendorsUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, Flow<List<VendorEntity>>>(dispatcher) {

    override suspend fun execute(params: Unit): Flow<List<VendorEntity>> {
        return repo.getVendors()
    }
}