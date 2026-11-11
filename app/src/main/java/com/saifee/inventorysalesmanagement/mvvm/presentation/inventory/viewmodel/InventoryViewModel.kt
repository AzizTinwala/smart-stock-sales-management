package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.viewmodel

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseViewModel
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.UiState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AddItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AdjustStockUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.DeleteItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.UpdateItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemGroupUseCases
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val itemUseCase: ItemUseCases,
    private val groupUseCases: ItemGroupUseCases,
    private val tenantRepo: TenantRepository
) : BaseViewModel() {
    val actionState = MutableStateFlow<UiState<Any>>(UiState.Idle)
    
    private val _items = MutableStateFlow<List<ItemEntity>>(emptyList())
    val items: StateFlow<List<ItemEntity>> = _items.asStateFlow()

    private val _groups = MutableStateFlow<List<ItemGroupEntity>>(emptyList())
    val groups: StateFlow<List<ItemGroupEntity>> = _groups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _adjustingItem = MutableStateFlow<ItemEntity?>(null)
    val adjustingItem: StateFlow<ItemEntity?> = _adjustingItem.asStateFlow()

    private val _isCompanyWise = MutableStateFlow(false)
    val isCompanyWise: StateFlow<Boolean> = _isCompanyWise.asStateFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        observeItems()
        loadGroups()
    }

    private fun observeItems() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            
            val itemsFlow = combine(
                _isCompanyWise,
                tenantRepo.selectedCompanyIdFlow,
                tenantRepo.selectedLocationIdFlow
            ) { companyWise, cid, lid ->
                Triple(companyWise, cid, lid)
            }.flatMapLatest { (companyWise, cid, lid) ->
                if (companyWise && cid != null) {
                    itemUseCase.getByCompany(cid).fold(
                        onSuccess = { flow -> 
                            flow.map { list ->
                                // Aggregate items with same name/sku
                                list.groupBy { it.itemName.lowercase() + (it.sku ?: "") }
                                    .map { (_, items) ->
                                        val first = items.first()
                                        first.copy(
                                            stockQty = items.sumOf { it.stockQty },
                                            locationId = "ALL" // Mark as aggregated
                                        )
                                    }
                            }
                        },
                        onError = { flowOf(emptyList()) },
                        onLoading = { flowOf(emptyList()) }
                    )
                } else {
                    itemUseCase.getAll(Unit).fold(
                        onSuccess = { it },
                        onError = { flowOf(emptyList()) },
                        onLoading = { flowOf(emptyList()) }
                    )
                }
            }

            itemsFlow.collectLatest { itemList ->
                _items.value = itemList
                _isLoading.value = false
            }
        }
    }

    fun setCompanyWise(enabled: Boolean) {
        _isCompanyWise.value = enabled
        observeItems()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            when (val result = groupUseCases.getAll(Unit)) {
                is ResultState.Success -> {
                    result.data.collect { groupList ->
                        _groups.value = groupList
                    }
                }
                else -> {}
            }
        }
    }

    fun setAdjustingItem(item: ItemEntity?) {
        _adjustingItem.value = item
    }

    fun adjustStock(itemId: Long, quantity: Double, reason: String, remark: String = "") {
        execute(
            actionState,
            itemUseCase.adjustStock,
            AdjustStockUseCase.AdjustStockParams(itemId, quantity, reason, remark),
            onSuccess = {
                setAdjustingItem(null)
            }
        )
    }

    // ---------- ADD ----------
    fun addItem(item: ItemEntity) {
        execute(
            actionState,
            itemUseCase.add,
            AddItemUseCase.AddItemParams(item),
        )
    }

    // ---------- Update ----------
    fun updateItem(item: ItemEntity) {
        execute(
            actionState,
            itemUseCase.update,
            UpdateItemUseCase.UpdateItemParams(item),
        )
    }

    // ---------- DELETE ----------
    fun deleteItem(item: ItemEntity) {
        execute(
            actionState,
            itemUseCase.delete,
            DeleteItemUseCase.DeleteItemParams(item),
        )
    }
}
