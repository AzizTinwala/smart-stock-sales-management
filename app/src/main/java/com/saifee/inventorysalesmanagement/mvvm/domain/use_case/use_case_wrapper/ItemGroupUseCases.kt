package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.AddGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.DeleteGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.GetGroupsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.GetItemGroupByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.UpdateGroupUseCase
import javax.inject.Inject

data class ItemGroupUseCases @Inject constructor(
    val add: AddGroupUseCase,
    val update: UpdateGroupUseCase,
    val delete: DeleteGroupUseCase,
    val getAll: GetGroupsUseCase,
    val getById: GetItemGroupByIdUseCase
)