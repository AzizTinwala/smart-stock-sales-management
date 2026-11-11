package com.saifee.inventorysalesmanagement.mvvm.presentation.payments.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.core.enums.PaymentMode
import com.saifee.inventorysalesmanagement.core.enums.PaymentType
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentReceivedEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentsOutEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.CustomerUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.VendorUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class PaymentUiItem(
    val id: String,
    val type: PaymentType,
    val partyName: String,
    val amount: Double,
    val mode: String,
    val date: Long,
    val reference: String?
)

data class PaymentsUiState(
    val payments: List<PaymentUiItem> = emptyList(),
    val filteredPayments: List<PaymentUiItem> = emptyList(),
    val customers: List<CustomerEntity> = emptyList(),
    val vendors: List<VendorEntity> = emptyList(),
    val searchQuery: String = "",
    val filterType: String = "ALL", // ALL, RECEIVED, PAID
    val totalReceived: Double = 0.0,
    val totalPaid: Double = 0.0,
    val netCash: Double = 0.0,
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val isSaving: Boolean = false
)

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val tradingRepo: TradingRepository,
    private val customerUseCases: CustomerUseCases,
    private val vendorUseCases: VendorUseCases,
    private val tenantRepo: TenantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentsUiState())
    val uiState: StateFlow<PaymentsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val customersFlow = customerUseCases.getAll(Unit).let { result ->
                if (result is ResultState.Success) result.data else flowOf(emptyList())
            }
            
            val vendorsFlow = vendorUseCases.getAll(Unit).let { result ->
                if (result is ResultState.Success) result.data else flowOf(emptyList())
            }

            combine(
                tradingRepo.getPaymentsReceived(),
                tradingRepo.getPaymentsOut(),
                customersFlow,
                vendorsFlow
            ) { received, out, customers, vendors ->
                
                val items = mutableListOf<PaymentUiItem>()
                var totalRec = 0.0
                var totalP = 0.0
                
                received.forEach { p ->
                    val party = customers.find { it.customerId == p.customerId }?.name ?: "Unknown"
                    items.add(PaymentUiItem(p.paymentReceivedId, PaymentType.OUT, party, p.amount, p.paymentMode, p.createdAt, p.reference))
                    totalRec += p.amount
                }
                
                out.forEach { p ->
                    val party = vendors.find { it.vendorId == p.vendorId }?.name ?: "Unknown"
                    items.add(PaymentUiItem(p.paymentOutId, PaymentType.IN, party, p.amount, p.paymentMode, p.createdAt, p.reference))
                    totalP += p.amount
                }

                val sortedItems = items.sortedByDescending { it.date }

                _uiState.update { state ->
                    val query = state.searchQuery.trim()
                    val filter = state.filterType
                    
                    val filtered = sortedItems.filter { item ->
                        val matchesSearch = item.partyName.contains(query, ignoreCase = true) ||
                                (item.reference?.contains(query, ignoreCase = true) == true)
                        val matchesFilter = when (filter) {
                            "RECEIVED" -> item.type == PaymentType.OUT
                            "PAID" -> item.type == PaymentType.IN
                            else -> true
                        }
                        matchesSearch && matchesFilter
                    }

                    state.copy(
                        payments = sortedItems,
                        filteredPayments = filtered,
                        customers = customers,
                        vendors = vendors,
                        totalReceived = totalRec,
                        totalPaid = totalP,
                        netCash = totalRec - totalP,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val filtered = state.payments.filter { item ->
                val matchesSearch = item.partyName.contains(query, ignoreCase = true) ||
                        (item.reference?.contains(query, ignoreCase = true) == true)
                val matchesFilter = when (state.filterType) {
                    "RECEIVED" -> item.type == PaymentType.OUT
                    "PAID" -> item.type == PaymentType.IN
                    else -> true
                }
                matchesSearch && matchesFilter
            }
            state.copy(searchQuery = query, filteredPayments = filtered)
        }
    }

    fun onFilterTypeChange(filter: String) {
        _uiState.update { state ->
            val filtered = state.payments.filter { item ->
                val matchesSearch = item.partyName.contains(state.searchQuery, ignoreCase = true) ||
                        (item.reference?.contains(state.searchQuery, ignoreCase = true) == true)
                val matchesFilter = when (filter) {
                    "RECEIVED" -> item.type == PaymentType.OUT
                    "PAID" -> item.type == PaymentType.IN
                    else -> true
                }
                matchesSearch && matchesFilter
            }
            state.copy(filterType = filter, filteredPayments = filtered)
        }
    }

    fun addPayment(type: PaymentType, partyId: String, amount: Double, mode: PaymentMode, reference: String?) {
        if (type == PaymentType.OUT) addPaymentIn(partyId, amount, mode.name, reference)
        else addPaymentOut(partyId, amount, mode.name, reference)
    }

    private fun addPaymentIn(customerId: String, amount: Double, mode: String, reference: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val payment = PaymentReceivedEntity(
                paymentReceivedId = UUID.randomUUID().toString(),
                companyId = "",
                locationId = "",
                customerId = customerId,
                amount = amount,
                paymentMode = mode,
                reference = reference,
                salesId = null,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            tradingRepo.addPaymentReceived(payment)
            _uiState.update { it.copy(isSaving = false, showAddDialog = false) }
        }
    }

    private fun addPaymentOut(vendorId: String, amount: Double, mode: String, reference: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val payment = PaymentsOutEntity(
                paymentOutId = UUID.randomUUID().toString(),
                companyId = "",
                locationId = "",
                vendorId = vendorId,
                amount = amount,
                paymentMode = mode,
                reference = reference,
                purchaseId = null,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            tradingRepo.addPaymentOut(payment)
            _uiState.update { it.copy(isSaving = false, showAddDialog = false) }
        }
    }

    fun setShowAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }
}
