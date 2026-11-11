package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.payment.AddPaymentOutUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.payment.AddPaymentReceivedUseCase
import javax.inject.Inject

data class PaymentUseCases @Inject constructor(
    val paymentIn: AddPaymentReceivedUseCase,
    val paymentOut: AddPaymentOutUseCase
)