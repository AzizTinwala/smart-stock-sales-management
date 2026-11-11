package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth

import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthorizationManagerTest {

    private lateinit var authManager: AuthorizationManager

    @Before
    fun setUp() {
        authManager = AuthorizationManager()
    }

    @Test
    fun `super user has full permissions across companies`() {
        val superUser = UserProfile(
            userId = "super1",
            role = UserRole.SUPER_USER,
            isApproved = true,
            isActive = true
        )

        assertTrue(authManager.canCreateCompany(superUser))
        assertTrue(authManager.canDeleteCompany(superUser))
        assertTrue(authManager.canViewData(superUser, "ANY_COMPANY"))
        assertTrue(authManager.canCreateUser(superUser, "ANY_COMPANY"))
    }

    @Test
    fun `unapproved or inactive user is denied permissions`() {
        val inactiveUser = UserProfile(
            userId = "user1",
            role = UserRole.ADMIN,
            isApproved = true,
            isActive = false,
            companyId = "COMP1"
        )

        assertFalse(authManager.canViewData(inactiveUser, "COMP1"))
        assertFalse(authManager.canCreateCompany(inactiveUser))
    }

    @Test
    fun `admin cannot self deactivate or escalate own role`() {
        val admin = UserProfile(
            userId = "admin1",
            role = UserRole.ADMIN,
            isApproved = true,
            isActive = true,
            companyId = "COMP1"
        )

        // Self deactivation should return false
        assertFalse(authManager.canDeactivateUser(admin, admin))

        // Self role modification should return false
        assertFalse(authManager.canChangeUserRole(admin, admin, UserRole.SUPER_USER))
    }

    @Test
    fun `cashier data isolation restricts viewing other companies`() {
        val cashier = UserProfile(
            userId = "cashier1",
            role = UserRole.CASHIER,
            isApproved = true,
            isActive = true,
            companyId = "COMP1"
        )

        assertTrue(authManager.canViewData(cashier, "COMP1"))
        assertFalse(authManager.canViewData(cashier, "COMP2"))
        assertFalse(authManager.canCreateCompany(cashier))
    }
}
