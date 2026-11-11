package com.saifee.inventorysalesmanagement.mvvm.presentation.item.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.saifee.inventorysalesmanagement.MainDispatcherRule
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AddItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.GetGroupsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemGroupUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import com.saifee.inventorysalesmanagement.mvvm.core.common_utils.LocalFileHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
class AddItemViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var itemUseCases: ItemUseCases

    @Mock
    private lateinit var groupUseCases: ItemGroupUseCases

    @Mock
    private lateinit var addItemUseCase: AddItemUseCase

    @Mock
    private lateinit var getGroupsUseCase: GetGroupsUseCase

    @Mock
    private lateinit var tenantRepository: TenantRepository

    @Mock
    private lateinit var syncRepository: SyncRepository

    @Mock
    private lateinit var localFileHelper: LocalFileHelper

    private lateinit var viewModel: AddItemViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle()

        runBlocking {
            whenever(itemUseCases.add).thenReturn(addItemUseCase)
            whenever(groupUseCases.getAll).thenReturn(getGroupsUseCase)

            // Mock group loading to avoid crash or empty state
            whenever(getGroupsUseCase(any())).thenReturn(ResultState.Success(flowOf(emptyList())))
            whenever(tenantRepository.selectedCompanyIdFlow).thenReturn(flowOf("COMP_A"))
            whenever(tenantRepository.getSelectedCompanyId()).thenReturn("COMP_A")
            whenever(tenantRepository.getSelectedLocationId()).thenReturn("LOC_A")

            viewModel = AddItemViewModel(
                itemUseCases,
                groupUseCases,
                tenantRepository,
                syncRepository,
                localFileHelper,
                savedStateHandle
            )
        }
    }

    @Test
    fun `onItemNameChange should update state`() {
        // When
        viewModel.onItemNameChange("New Item")

        // Then
        assertEquals("New Item", viewModel.addItemUiState.value.itemName)
    }

    @Test
    fun `saveItem should show error when fields are blank`() {
        // When
        viewModel.saveItem {}

        // Then
        assertEquals("Item name is required", viewModel.addItemUiState.value.itemNameError)
        assertEquals("Please select item group", viewModel.addItemUiState.value.itemGroupError)
        assertEquals("Cost price is required", viewModel.addItemUiState.value.costPriceError)
    }

    @Test
    fun `saveItem should call AddItemUseCase when valid`() = runTest {
        // Given
        val group = ItemGroupEntity(1L, "c1", "l1", "Group 1", null, 0L, 0L)
        runBlocking {
            whenever(getGroupsUseCase(any())).thenReturn(ResultState.Success(flowOf(listOf(group))))
        }
        
        // Re-init to load groups
        viewModel = AddItemViewModel(
            itemUseCases,
            groupUseCases,
            tenantRepository,
            syncRepository,
            localFileHelper,
            savedStateHandle
        )

        viewModel.onItemNameChange("Test Item")
        viewModel.onItemGroupChange("Group 1")
        viewModel.onCostPriceChange("100.0")
        
        runBlocking {
            whenever(addItemUseCase(any())).thenReturn(ResultState.Success(1L))
        }

        // When
        var successCalled = false
        viewModel.saveItem { successCalled = true }

        // Then
        verify(addItemUseCase).invoke(any())
        assertEquals(true, successCalled)
    }
}
