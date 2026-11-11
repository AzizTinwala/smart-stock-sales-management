package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.AddPurchaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.DeletePurchaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.GetPurchaseByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.GetPurchasesUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.UpdatePurchaseUseCase
import javax.inject.Inject

data class PurchaseUseCases @Inject constructor(
    val add: AddPurchaseUseCase,
    val update: UpdatePurchaseUseCase,
    val delete: DeletePurchaseUseCase,
    val getAll: GetPurchasesUseCase,
    val getById: GetPurchaseByIdUseCase
)
