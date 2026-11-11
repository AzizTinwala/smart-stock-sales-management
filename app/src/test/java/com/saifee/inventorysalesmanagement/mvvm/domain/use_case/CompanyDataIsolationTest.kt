package com.saifee.inventorysalesmanagement.mvvm.domain.use_case

import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CompanyDataIsolationTest {

    private lateinit var authorizationManager: AuthorizationManager

    private val superUser = UserProfile(
        userId = "super_1",
        companyId = "COMP_A",
        role = UserRole.SUPER_USER,
        isApproved = true,
        isActive = true
    )

    private val adminCompA = UserProfile(
        userId = "admin_a",
        companyId = "COMP_A",
        role = UserRole.ADMIN,
        isApproved = true,
        isActive = true
    )

    private val userCompA = UserProfile(
        userId = "user_a",
        companyId = "COMP_A",
        role = UserRole.USER,
        isApproved = true,
        isActive = true
    )

    @Before
    fun setUp() {
        authorizationManager = AuthorizationManager()
    }

    @Test
    fun testCrossCompanyDataAccess_AdminAndUserBlocked() {
        // Super user can access data for COMP_A, COMP_B, etc.
        assertTrue(authorizationManager.canViewData(superUser, "COMP_A"))
        assertTrue(authorizationManager.canViewData(superUser, "COMP_B"))

        // Admin and User of COMP_A can access COMP_A data
        assertTrue(authorizationManager.canViewData(adminCompA, "COMP_A"))
        assertTrue(authorizationManager.canViewData(userCompA, "COMP_A"))

        // Admin and User of COMP_A cannot access COMP_B data (Cross-company access blocked)
        assertFalse(authorizationManager.canViewData(adminCompA, "COMP_B"))
        assertFalse(authorizationManager.canViewData(userCompA, "COMP_B"))
    }

    @Test
    fun testCrossCompanyDataCreation_Blocked() {
        // Admin and User cannot create data for another company
        assertFalse(authorizationManager.canCreateData(adminCompA, "COMP_B"))
        assertFalse(authorizationManager.canCreateData(userCompA, "COMP_B"))

        // Super user can create data for another company
        assertTrue(authorizationManager.canCreateData(superUser, "COMP_B"))
    }
}
