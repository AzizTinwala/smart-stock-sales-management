package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddGroupUseCase @Inject constructor(
    private val repo: ItemRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AddGroupUseCase.AddGroupParams, Long>(dispatcher) {

    override suspend fun execute(params: AddGroupParams): Long {
        return repo.addItemGroup(params.group)
    }
    data class AddGroupParams(val group: ItemGroupEntity)
}
