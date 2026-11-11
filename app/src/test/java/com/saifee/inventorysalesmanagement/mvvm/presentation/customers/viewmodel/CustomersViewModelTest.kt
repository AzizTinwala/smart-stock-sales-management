package com.saifee.inventorysalesmanagement.mvvm.presentation.customers.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.saifee.inventorysalesmanagement.MainDispatcherRule
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.GetCustomersUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.CustomerUseCases
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
class CustomersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var useCases: CustomerUseCases

    @Mock
    private lateinit var repository: TenantRepository

    @Mock
    private lateinit var getCustomersUseCase: GetCustomersUseCase

    private lateinit var viewModel: CustomersViewModel
    private val selectedCompanyIdFlow = MutableStateFlow<String?>(null)
    private val selectedLocationIdFlow = MutableStateFlow<String?>(null)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        runBlocking {
            whenever(repository.selectedCompanyIdFlow).thenReturn(selectedCompanyIdFlow)
            whenever(repository.selectedLocationIdFlow).thenReturn(selectedLocationIdFlow)
            whenever(useCases.getAll).thenReturn(getCustomersUseCase)

            whenever(getCustomersUseCase(any())).thenReturn(ResultState.Success(flowOf(emptyList())))

            viewModel = CustomersViewModel(useCases, repository, SavedStateHandle())
        }
    }

    @Test
    fun `loadCustomers should update state on success`() = runTest {
        // Given
        val customers = listOf(
            CustomerEntity(
                customerId = "c1", name = "John Doe", phone = "123", email = "john@example.com", 
                address = "Address", companyId = "cmp1", locationId = "loc1", 
                createdAt = 0L, updatedAt = 0L
            )
        )
        whenever(getCustomersUseCase(any())).thenReturn(ResultState.Success(flowOf(customers)))

        // When
        selectedCompanyIdFlow.value = "cmp1"
        selectedLocationIdFlow.value = "loc1"

        // Then
        assertEquals(customers, viewModel.uiState.value.customers)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onNameChange should update UI state`() {
        // When
        viewModel.onNameChange("Jane Doe")

        // Then
        assertEquals("Jane Doe", viewModel.uiState.value.name)
    }
}
