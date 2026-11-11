package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val repo: ItemRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, Flow<List<ItemEntity>>>(dispatcher) {
    override suspend fun execute(params: Unit): Flow<List<ItemEntity>> {
        return repo.getItems()
    }
}