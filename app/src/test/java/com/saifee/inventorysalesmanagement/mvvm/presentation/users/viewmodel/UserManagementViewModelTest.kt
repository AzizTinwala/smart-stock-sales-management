package com.saifee.inventorysalesmanagement.mvvm.presentation.users.viewmodel

import com.saifee.inventorysalesmanagement.MainDispatcherRule
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.UserManagementRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user.GetUsersUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user.ManageUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class UserManagementViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getUsersUseCase: GetUsersUseCase = mock(GetUsersUseCase::class.java)
    private val manageUserUseCase: ManageUserUseCase = mock(ManageUserUseCase::class.java)
    private val authRepository: AuthRepository = mock(AuthRepository::class.java)
    private val tenantRepository: TenantRepository = mock(TenantRepository::class.java)
    private val userManagementRepository: UserManagementRepository = mock(UserManagementRepository::class.java)

    private lateinit var viewModel: UserManagementViewModel

    private val adminUser = UserProfile(
        userId = "admin_1",
        email = "admin@compa.com",
        name = "Admin User",
        companyId = "COMP_A",
        role = UserRole.ADMIN,
        isApproved = true,
        isActive = true
    )

    @Before
    fun setUp() = runTest {
        `when`(authRepository.getCurrentUserProfile()).thenReturn(adminUser)
        `when`(authRepository.currentUserProfileFlow).thenReturn(flowOf(adminUser))
        `when`(tenantRepository.getCompanies()).thenReturn(flowOf(emptyList()))
        `when`(tenantRepository.selectedCompanyIdFlow).thenReturn(flowOf("COMP_A"))
        `when`(getUsersUseCase(any())).thenReturn(ResultState.Success(flowOf(emptyList())))

        viewModel = UserManagementViewModel(
            getUsersUseCase,
            manageUserUseCase,
            authRepository,
            tenantRepository,
            userManagementRepository
        )
    }

    @Test
    fun testSearchQueryFiltering() {
        viewModel.onSearchQueryChanged("Alice")
        assertEquals("Alice", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun testRoleFilter() {
        viewModel.onRoleFilterChanged(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, viewModel.uiState.value.roleFilter)
    }
}
