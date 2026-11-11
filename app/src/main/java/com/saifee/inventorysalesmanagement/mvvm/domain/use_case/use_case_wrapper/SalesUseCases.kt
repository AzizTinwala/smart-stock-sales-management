package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.AddSaleUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.DeleteSaleUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.GetSaleByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.GetInvoiceDetailUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.GetSalesUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.UpdateSaleUseCase
import javax.inject.Inject

data class SalesUseCases @Inject constructor(
    val add: AddSaleUseCase,
    val update: UpdateSaleUseCase,
    val delete: DeleteSaleUseCase,
    val getAll: GetSalesUseCase,
    val getById: GetSaleByIdUseCase,
    val getInvoiceDetail: GetInvoiceDetailUseCase
)
