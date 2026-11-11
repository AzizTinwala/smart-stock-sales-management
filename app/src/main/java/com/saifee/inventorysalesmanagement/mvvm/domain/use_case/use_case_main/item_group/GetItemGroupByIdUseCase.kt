package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetItemGroupByIdUseCase @Inject constructor(
    private val repo: ItemRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Long, ItemGroupEntity?>(dispatcher) {
    override suspend fun execute(params: Long): ItemGroupEntity? {
        return repo.getItemGroupById(params)
    }
}
