package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.inventory

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.InventoryLogEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetInventoryLogsUseCase @Inject constructor(
    private val repo: ItemRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, List<InventoryLogEntity>>(dispatcher) {
    override suspend fun execute(params: Unit): List<InventoryLogEntity> {
        //return repo.getInventoryLogs()
        return ArrayList()
    }

}