package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.AddCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.DeleteCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.GetCustomerByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.GetCustomersUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.UpdateCustomerUseCase
import javax.inject.Inject

data class CustomerUseCases @Inject constructor(
    val add: AddCustomerUseCase,
    val update: UpdateCustomerUseCase,
    val delete: DeleteCustomerUseCase,
    val getAll: GetCustomersUseCase,
    val getById: GetCustomerByIdUseCase
)
