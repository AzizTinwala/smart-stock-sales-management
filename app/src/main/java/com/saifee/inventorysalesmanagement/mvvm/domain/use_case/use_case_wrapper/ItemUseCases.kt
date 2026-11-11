package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AddItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AdjustStockUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.DeleteItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.GetItemByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.GetItemsByCompanyUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.GetItemsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.UpdateItemUseCase
import javax.inject.Inject


data class ItemUseCases @Inject constructor(
    val add: AddItemUseCase,
    val update: UpdateItemUseCase,
    val delete: DeleteItemUseCase,
    val getAll: GetItemsUseCase,
    val getById: GetItemByIdUseCase,
    val adjustStock: AdjustStockUseCase,
    val getByCompany: GetItemsByCompanyUseCase
)