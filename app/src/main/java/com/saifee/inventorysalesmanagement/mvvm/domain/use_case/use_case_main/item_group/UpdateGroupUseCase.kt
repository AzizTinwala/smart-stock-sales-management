package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(
    private val repo: ItemRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<UpdateGroupUseCase.UpdateGroupParams, Unit>(dispatcher) {

    override suspend fun execute(params: UpdateGroupParams) {
        repo.addItemGroup(params.group)
    }
    data class UpdateGroupParams(val group: ItemGroupEntity)
}