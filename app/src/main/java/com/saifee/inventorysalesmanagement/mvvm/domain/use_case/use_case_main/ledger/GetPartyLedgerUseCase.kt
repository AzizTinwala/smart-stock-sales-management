package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.ledger

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerEntry
import com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerTransactionType
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetPartyLedgerUseCase @Inject constructor(
    private val repo: TradingRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<GetPartyLedgerUseCase.Params, Flow<List<LedgerEntry>>>(dispatcher) {

    data class Params(
        val partyId: String,
        val isCustomer: Boolean,
        val startDate: Long? = null,
        val endDate: Long? = null
    )

    override suspend fun execute(params: Params): Flow<List<LedgerEntry>> {
        if (params.partyId.isBlank()) return flowOf(emptyList())

        return if (params.isCustomer) {
            val customer = repo.getCustomerById(params.partyId)
            val openingBalance = customer?.openingBalance ?: 0.0
            
            combine(
                repo.getSales(),
                repo.getPaymentsReceived()
            ) { salesList, paymentsList ->
                val entries = mutableListOf<LedgerEntry>()
                
                // Add Opening Balance as first entry
                entries.add(
                    LedgerEntry(
                        transactionId = "OP-${params.partyId}",
                        date = customer?.createdAt ?: 0L,
                        type = LedgerTransactionType.OPENING_BALANCE,
                        referenceNumber = "OB-000",
                        description = "Opening Ledger Balance",
                        debitAmount = if (openingBalance >= 0) openingBalance else 0.0,
                        creditAmount = if (openingBalance < 0) -openingBalance else 0.0,
                        runningBalance = openingBalance
                    )
                )

                // Filter and map Sales
                salesList.filter { it.customerId == params.partyId && !it.isDeleted }.forEach { s ->
                    entries.add(
                        LedgerEntry(
                            transactionId = s.salesId,
                            date = s.createdAt,
                            type = LedgerTransactionType.SALE,
                            referenceNumber = s.invoiceNumber,
                            description = "Sales Invoice standard billing",
                            debitAmount = s.finalAmount,
                            creditAmount = 0.0,
                            runningBalance = 0.0
                        )
                    )
                }

                // Filter and map Payments Received
                paymentsList.filter { it.customerId == params.partyId }.forEach { p ->
                    entries.add(
                        LedgerEntry(
                            transactionId = p.paymentReceivedId,
                            date = p.createdAt,
                            type = LedgerTransactionType.PAYMENT_RECEIVED,
                            referenceNumber = p.reference ?: "CASH-DIR",
                            description = "Payment Received via ${p.paymentMode}",
                            debitAmount = 0.0,
                            creditAmount = p.amount,
                            runningBalance = 0.0
                        )
                    )
                }

                // Sort chronologically by date
                val sortedTransactions = entries.drop(1).sortedBy { it.date }
                
                val finalEntries = mutableListOf<LedgerEntry>()
                finalEntries.add(entries.first())
                
                var currentBalance = openingBalance
                sortedTransactions.forEach { entry ->
                    currentBalance += (entry.debitAmount - entry.creditAmount)
                    finalEntries.add(entry.copy(runningBalance = currentBalance))
                }

                // Filter by date range if provided
                finalEntries.filter { item ->
                    val afterStart = params.startDate == null || item.date >= params.startDate
                    val beforeEnd = params.endDate == null || item.date <= params.endDate
                    // Always keep opening balance visible or filter strictly
                    afterStart && beforeEnd || item.type == LedgerTransactionType.OPENING_BALANCE
                }
            }
        } else {
            val vendor = repo.getVendorById(params.partyId)
            val openingBalance = vendor?.openingBalance ?: 0.0

            combine(
                repo.getPurchases(),
                repo.getPaymentsOut()
            ) { purchasesList, paymentsList ->
                val entries = mutableListOf<LedgerEntry>()

                entries.add(
                    LedgerEntry(
                        transactionId = "OP-${params.partyId}",
                        date = vendor?.createdAt ?: 0L,
                        type = LedgerTransactionType.OPENING_BALANCE,
                        referenceNumber = "OB-000",
                        description = "Opening Ledger Balance",
                        debitAmount = if (openingBalance < 0) -openingBalance else 0.0,
                        creditAmount = if (openingBalance >= 0) openingBalance else 0.0,
                        runningBalance = openingBalance
                    )
                )

                // Purchases increase payable balance (Credit for vendor account)
                purchasesList.filter { it.vendorId == params.partyId && !it.isDeleted }.forEach { p ->
                    entries.add(
                        LedgerEntry(
                            transactionId = p.purchaseId,
                            date = p.createdAt,
                            type = LedgerTransactionType.PURCHASE,
                            referenceNumber = p.invoiceNumber,
                            description = "Purchase Invoice inward goods",
                            debitAmount = 0.0,
                            creditAmount = p.totalAmount,
                            runningBalance = 0.0
                        )
                    )
                }

                // Payments reduce payable balance (Debit for vendor account)
                paymentsList.filter { it.vendorId == params.partyId }.forEach { p ->
                    entries.add(
                        LedgerEntry(
                            transactionId = p.paymentOutId,
                            date = p.createdAt,
                            type = LedgerTransactionType.PAYMENT_OUT,
                            referenceNumber = p.reference ?: "CASH-DIR",
                            description = "Payment Settled via ${p.paymentMode}",
                            debitAmount = p.amount,
                            creditAmount = 0.0,
                            runningBalance = 0.0
                        )
                    )
                }

                val sortedTransactions = entries.drop(1).sortedBy { it.date }

                val finalEntries = mutableListOf<LedgerEntry>()
                finalEntries.add(entries.first())

                var currentBalance = openingBalance
                sortedTransactions.forEach { entry ->
                    // For Vendor statement: Credits increase liability balance, debits decrease it
                    currentBalance += (entry.creditAmount - entry.debitAmount)
                    finalEntries.add(entry.copy(runningBalance = currentBalance))
                }

                finalEntries.filter { item ->
                    val afterStart = params.startDate == null || item.date >= params.startDate
                    val beforeEnd = params.endDate == null || item.date <= params.endDate
                    afterStart && beforeEnd || item.type == LedgerTransactionType.OPENING_BALANCE
                }
            }
        }
    }
}
