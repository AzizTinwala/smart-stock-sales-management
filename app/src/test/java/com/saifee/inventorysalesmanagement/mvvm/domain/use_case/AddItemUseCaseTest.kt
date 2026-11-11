package com.saifee.inventorysalesmanagement.mvvm.domain.use_case

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AddItemUseCase
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
class AddItemUseCaseTest {

    @Mock
    private lateinit var repository: ItemRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var addItemUseCase: AddItemUseCase
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
        addItemUseCase = AddItemUseCase(repository, authRepository, authorizationManager, testDispatcher)
    }

    @Test
    fun `execute should call repository addItem`() = runTest(testDispatcher) {
        // Given
        val item = ItemEntity(
            itemId = 1L,
            companyId = "c1",
            locationId = "l1",
            itemName = "Test",
            groupId = 1L,
            sku = null,
            barcode = null,
            purchasePrice = 10.0,
            sellingPrice = 20.0,
            stockQty = 0.0,
            unit = "pcs",
            createdAt = 0L,
            updatedAt = 0L
        )
        val params = AddItemUseCase.AddItemParams(item)

        // When
        addItemUseCase(params)

        // Then
        verify(repository).addItem(item)
    }
}
