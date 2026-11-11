package com.saifee.inventorysalesmanagement.mvvm.presentation.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerEntry
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.GetCustomersUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.ledger.GetPartyLedgerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.GetVendorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartyLedgerUiState(
    val isCustomerView: Boolean = true,
    val selectedPartyId: String = "",
    val partyName: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val ledgerEntries: List<LedgerEntry> = emptyList(),
    val customers: List<CustomerEntity> = emptyList(),
    val vendors: List<VendorEntity> = emptyList(),
    
    // Summary KPI Blocks
    val openingBalanceTotal: Double = 0.0,
    val totalInvoicedAmount: Double = 0.0,
    val totalSettledAmount: Double = 0.0,
    val closingBalance: Double = 0.0,
    
    val isLoading: Boolean = false
)

@HiltViewModel
class PartyLedgerViewModel @Inject constructor(
    private val getPartyLedgerUseCase: GetPartyLedgerUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val getVendorsUseCase: GetVendorsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PartyLedgerUiState())
    val uiState: StateFlow<PartyLedgerUiState> = _uiState.asStateFlow()

    private var ledgerJob: Job? = null

    init {
        loadParties()
    }

    private fun loadParties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val customersFlow = getCustomersUseCase(Unit).let { result ->
                if (result is ResultState.Success) result.data else flowOf(emptyList())
            }
            
            val vendorsFlow = getVendorsUseCase(Unit).let { result ->
                if (result is ResultState.Success) result.data else flowOf(emptyList())
            }

            launch {
                customersFlow.collectLatest { list ->
                    _uiState.update { it.copy(customers = list) }
                }
            }

            launch {
                vendorsFlow.collectLatest { list ->
                    _uiState.update { it.copy(vendors = list, isLoading = false) }
                }
            }
        }
    }

    fun onTogglePartyType(isCustomer: Boolean) {
        _uiState.update { it.copy(
            isCustomerView = isCustomer,
            selectedPartyId = "",
            partyName = "",
            ledgerEntries = emptyList(),
            openingBalanceTotal = 0.0,
            totalInvoicedAmount = 0.0,
            totalSettledAmount = 0.0,
            closingBalance = 0.0
        ) }
        observeLedger()
    }

    fun onPartySelected(id: String, name: String) {
        _uiState.update { it.copy(selectedPartyId = id, partyName = name) }
        observeLedger()
    }

    fun onDateRangeSelected(start: Long?, end: Long?) {
        _uiState.update { it.copy(startDate = start, endDate = end) }
        observeLedger()
    }

    private fun observeLedger() {
        ledgerJob?.cancel()
        val state = _uiState.value
        if (state.selectedPartyId.isBlank()) return

        ledgerJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val ledgerParams = GetPartyLedgerUseCase.Params(
                partyId = state.selectedPartyId,
                isCustomer = state.isCustomerView,
                startDate = state.startDate,
                endDate = state.endDate
            )

            when (val result = getPartyLedgerUseCase(ledgerParams)) {
                is ResultState.Success -> {
                    result.data.collectLatest { entries ->
                        var totalInvoiced = 0.0
                        var totalSettled = 0.0
                        val opBal = entries.firstOrNull { it.type == com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerTransactionType.OPENING_BALANCE }?.runningBalance ?: 0.0
                        
                        entries.forEach { entry ->
                            if (state.isCustomerView) {
                                if (entry.type == com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerTransactionType.SALE) {
                                    totalInvoiced += entry.debitAmount
                                } else if (entry.type == com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerTransactionType.PAYMENT_RECEIVED) {
                                    totalSettled += entry.creditAmount
                                }
                            } else {
                                if (entry.type == com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerTransactionType.PURCHASE) {
                                    totalInvoiced += entry.creditAmount
                                } else if (entry.type == com.saifee.inventorysalesmanagement.mvvm.domain.model.LedgerTransactionType.PAYMENT_OUT) {
                                    totalSettled += entry.debitAmount
                                }
                            }
                        }

                        val closeBal = entries.lastOrNull()?.runningBalance ?: opBal

                        _uiState.update { it.copy(
                            ledgerEntries = entries,
                            openingBalanceTotal = opBal,
                            totalInvoicedAmount = totalInvoiced,
                            totalSettledAmount = totalSettled,
                            closingBalance = closeBal,
                            isLoading = false
                        ) }
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
                else -> {}
            }
        }
    }
}
