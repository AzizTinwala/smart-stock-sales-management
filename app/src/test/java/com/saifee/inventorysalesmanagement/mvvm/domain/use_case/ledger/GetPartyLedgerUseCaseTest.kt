package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.ledger

import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentReceivedEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerTransactionType
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetPartyLedgerUseCaseTest {

    @Mock
    private lateinit var tradingRepo: TradingRepository

    private lateinit var getPartyLedgerUseCase: GetPartyLedgerUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getPartyLedgerUseCase = GetPartyLedgerUseCase(tradingRepo, Dispatchers.Unconfined)
    }

    @Test
    fun `customer ledger calculates running balance correctly across sales and payments`() = runTest {
        val customerId = "CUST1"

        val customer = CustomerEntity(
            customerId = customerId,
            name = "Test Customer",
            openingBalance = 500.0,
            createdAt = 1000L
        )

        val sale1 = SalesEntity(
            salesId = "S1",
            customerId = customerId,
            invoiceNumber = "INV-001",
            finalAmount = 1000.0,
            createdAt = 2000L,
            isDeleted = false
        )

        val payment1 = PaymentReceivedEntity(
            paymentReceivedId = "PAY1",
            customerId = customerId,
            amount = 800.0,
            paymentMode = "CASH",
            createdAt = 3000L
        )

        `when`(tradingRepo.getCustomerById(customerId)).thenReturn(customer)
        `when`(tradingRepo.getSales()).thenReturn(flowOf(listOf(sale1)))
        `when`(tradingRepo.getPaymentsReceived()).thenReturn(flowOf(listOf(payment1)))

        val params = GetPartyLedgerUseCase.Params(partyId = customerId, isCustomer = true)
        val resultState = getPartyLedgerUseCase(params)

        assertTrue(resultState is ResultState.Success)
        val ledgerFlow = (resultState as ResultState.Success).data
        val ledger = ledgerFlow.first()

        assertEquals(3, ledger.size)

        // Entry 1: Opening Balance
        assertEquals(LedgerTransactionType.OPENING_BALANCE, ledger[0].type)
        assertEquals(500.0, ledger[0].runningBalance, 0.001)

        // Entry 2: Sale (+1000 Debit)
        assertEquals(LedgerTransactionType.SALE, ledger[1].type)
        assertEquals(1000.0, ledger[1].debitAmount, 0.001)
        assertEquals(1500.0, ledger[1].runningBalance, 0.001)

        // Entry 3: Payment (-800 Credit)
        assertEquals(LedgerTransactionType.PAYMENT_RECEIVED, ledger[2].type)
        assertEquals(800.0, ledger[2].creditAmount, 0.001)
        assertEquals(700.0, ledger[2].runningBalance, 0.001)
    }
}
