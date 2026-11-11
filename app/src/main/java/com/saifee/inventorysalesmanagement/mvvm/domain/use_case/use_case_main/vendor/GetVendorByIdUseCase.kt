package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetVendorByIdUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<String, VendorEntity?>(dispatcher) {
    override suspend fun execute(params: String): VendorEntity? {
        return repo.getVendorById(params)
    }
}
