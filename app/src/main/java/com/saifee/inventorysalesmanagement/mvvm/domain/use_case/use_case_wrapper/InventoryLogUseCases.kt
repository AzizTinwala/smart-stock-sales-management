package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.inventory.AddInventoryLogUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.inventory.GetInventoryLogsUseCase
import javax.inject.Inject

data class InventoryLogUseCases @Inject constructor(
    val add: AddInventoryLogUseCase,
    val getAll: GetInventoryLogsUseCase
)