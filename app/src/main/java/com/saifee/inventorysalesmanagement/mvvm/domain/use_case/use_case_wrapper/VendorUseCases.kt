package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.AddVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.DeleteVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.GetVendorByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.GetVendorsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.UpdateVendorUseCase
import javax.inject.Inject

data class VendorUseCases @Inject constructor(
    val add: AddVendorUseCase,
    val update: UpdateVendorUseCase,
    val delete: DeleteVendorUseCase,
    val getAll: GetVendorsUseCase,
    val getById: GetVendorByIdUseCase
)
