package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateVendorUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<UpdateVendorUseCase.UpdateVendorParams, Unit>(dispatcher) {
    override suspend fun execute(params: UpdateVendorParams) {
        repo.addVendor(params.vendor)
    }

    data class UpdateVendorParams(val vendor: VendorEntity)
}
