package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.viewmodel

import com.saifee.inventorysalesmanagement.MainDispatcherRule
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.GetItemsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.GetGroupsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemGroupUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var itemUseCases: ItemUseCases

    @Mock
    private lateinit var getItemsUseCase: GetItemsUseCase

    @Mock
    private lateinit var groupUseCases: ItemGroupUseCases

    @Mock
    private lateinit var getGroupsUseCase: GetGroupsUseCase

    @Mock
    private lateinit var tenantRepo: TenantRepository

    private lateinit var viewModel: InventoryViewModel
    private val selectedCompanyIdFlow = MutableStateFlow<String?>(null)
    private val selectedLocationIdFlow = MutableStateFlow<String?>(null)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        
        whenever(itemUseCases.getAll).thenReturn(getItemsUseCase)
        whenever(groupUseCases.getAll).thenReturn(getGroupsUseCase)

        runBlocking {
            whenever(tenantRepo.selectedCompanyIdFlow).thenReturn(selectedCompanyIdFlow)
            whenever(tenantRepo.selectedLocationIdFlow).thenReturn(selectedLocationIdFlow)
            whenever(getGroupsUseCase(any())).thenReturn(ResultState.Success(flowOf(emptyList())))
            whenever(getItemsUseCase(any())).thenReturn(ResultState.Success(flowOf(emptyList())))
        }

        viewModel = InventoryViewModel(itemUseCases, groupUseCases, tenantRepo)
    }

    @Test
    fun `getAllItem should update items list on success`() = runTest {
        // Given
        val itemList = listOf(
            ItemEntity(
                itemId = 1L, companyId = "c1", locationId = "l1", itemName = "Item 1", groupId = 1L,
                sku = null, barcode = null, purchasePrice = 10.0, sellingPrice = 20.0, stockQty = 5.0,
                unit = "pcs", createdAt = 0L, updatedAt = 0L
            )
        )
        whenever(getItemsUseCase(any())).thenReturn(ResultState.Success(flowOf(itemList)))

        // When
        selectedCompanyIdFlow.value = "c1"
        selectedLocationIdFlow.value = "l1"

        // Then
        assertEquals(itemList, viewModel.items.value)
        assertEquals(false, viewModel.isLoading.value)
    }
}
