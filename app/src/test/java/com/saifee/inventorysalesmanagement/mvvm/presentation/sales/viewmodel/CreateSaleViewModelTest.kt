package com.saifee.inventorysalesmanagement.mvvm.presentation.sales.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.GetCustomersUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.GetItemsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.CustomerUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.SalesUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class CreateSaleViewModelTest {

    @Mock private lateinit var salesUseCases: SalesUseCases
    @Mock private lateinit var itemUseCases: ItemUseCases
    @Mock private lateinit var customerUseCases: CustomerUseCases
    @Mock private lateinit var getCustomersUseCase: GetCustomersUseCase
    @Mock private lateinit var getItemsUseCase: GetItemsUseCase
    @Mock private lateinit var tenantRepository: TenantRepository
    @Mock private lateinit var sharedPref: SharedPref

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CreateSaleViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        `when`(sharedPref.defaultRoundOff).thenReturn(false)
        `when`(customerUseCases.getAll).thenReturn(getCustomersUseCase)
        `when`(itemUseCases.getAll).thenReturn(getItemsUseCase)
        runBlocking {
            `when`(getCustomersUseCase.invoke(Unit)).thenReturn(ResultState.Success(flowOf(emptyList())))
            `when`(getItemsUseCase.invoke(Unit)).thenReturn(ResultState.Success(flowOf(emptyList())))
        }

        viewModel = CreateSaleViewModel(
            salesUseCases = salesUseCases,
            itemUseCases = itemUseCases,
            customerUseCases = customerUseCases,
            tenantRepository = tenantRepository,
            sharedPref = sharedPref,
            savedStateHandle = SavedStateHandle()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveSale with empty cart sets error in uiState`() = runTest(testDispatcher) {
        var successCalled = false
        viewModel.saveSale { successCalled = true }

        assertEquals("Cart is empty", viewModel.uiState.value.error)
        assertEquals(false, successCalled)
    }

    @Test
    fun `addItemToCart sanitizes zero or negative quantity`() = runTest(testDispatcher) {
        val item = ItemEntity(itemId = 100L, itemName = "Test Item", sellingPrice = 50.0)

        viewModel.addItemToCart(item, -5.0)

        val cart = viewModel.uiState.value.cart
        assertEquals(1, cart.size)
        assertEquals(1.0, cart.first().quantity, 0.001)
    }

    @Test
    fun `updateCartItemPrice sanitizes negative price`() = runTest(testDispatcher) {
        val item = ItemEntity(itemId = 100L, itemName = "Test Item", sellingPrice = 50.0)
        viewModel.addItemToCart(item, 2.0)

        val cartItem = viewModel.uiState.value.cart.first()
        viewModel.updateCartItemPrice(cartItem, -20.0)

        val updatedCartItem = viewModel.uiState.value.cart.first()
        assertEquals(0.0, updatedCartItem.price, 0.001)
    }
}
