package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteGroupUseCase @Inject constructor(
    private val repo: ItemRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<DeleteGroupUseCase.DeleteGroupParams, Unit>(dispatcher) {

    override suspend fun execute(params: DeleteGroupParams) {
        repo.deleteItemGroup(params.group)
    }

    data class DeleteGroupParams(val group: ItemGroupEntity)
}