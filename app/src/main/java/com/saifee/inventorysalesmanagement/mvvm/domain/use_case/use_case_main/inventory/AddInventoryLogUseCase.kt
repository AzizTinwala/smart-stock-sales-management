package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.inventory

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.InventoryLogEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddInventoryLogUseCase @Inject constructor(
    private val repo: ItemRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AddInventoryLogUseCase.AddInventoryLogParams, Unit>(dispatcher) {

    override suspend fun execute(params: AddInventoryLogParams) {
        repo.addInventoryLog(params.log)
    }

    data class AddInventoryLogParams(val log: InventoryLogEntity)
}