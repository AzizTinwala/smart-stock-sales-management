package com.saifee.inventorysalesmanagement.mvvm.domain.use_case

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.AddSaleUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AddSaleUseCaseTest {

    @Mock
    private lateinit var repository: TradingRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var addSaleUseCase: AddSaleUseCase
    private lateinit var authorizationManager: AuthorizationManager
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authorizationManager = AuthorizationManager()
        runBlocking {
            `when`(authRepository.getCurrentUserProfile()).thenReturn(
                UserProfile(userId = "admin", companyId = "c1", role = UserRole.ADMIN, isApproved = true, isActive = true)
            )
        }
        addSaleUseCase = AddSaleUseCase(repository, authRepository, authorizationManager, testDispatcher)
    }

    @Test
    fun `invoke should call repository addSales`() = runTest(testDispatcher) {
        // Given
        val sale = SalesEntity(
            salesId = "s1", companyId = "c1", locationId = "l1", invoiceNumber = "INV", customerId = null,
            totalAmount = 100.0, discount = 0.0, tax = 0.0, finalAmount = 100.0, status = "PAID",
            createdAt = 0L, updatedAt = 0L
        )
        val item = SalesItemEntity(
            salesItemId = "s1_item1", companyId = "c1", locationId = "l1", salesId = "s1", itemId = 1L,
            quantity = 1.0, price = 100.0, total = 100.0, createdAt = 0L, updatedAt = 0L
        )
        val items = listOf(item)
        val params = AddSaleUseCase.AddSalesParam(sale, items)

        // When
        addSaleUseCase(params)

        // Then
        verify(repository).addSales(sale, items)
    }
}
