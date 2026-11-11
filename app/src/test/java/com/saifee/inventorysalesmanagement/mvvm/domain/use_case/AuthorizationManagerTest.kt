package com.saifee.inventorysalesmanagement.mvvm.domain.use_case

import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthorizationManagerTest {

    private lateinit var authorizationManager: AuthorizationManager

    private val superUser = UserProfile(
        userId = "super_1",
        email = "super@app.com",
        companyId = "COMP_A",
        role = UserRole.SUPER_USER,
        isApproved = true,
        isActive = true
    )

    private val adminCompA = UserProfile(
        userId = "admin_a",
        email = "admin@compa.com",
        companyId = "COMP_A",
        role = UserRole.ADMIN,
        isApproved = true,
        isActive = true
    )

    private val userCompA = UserProfile(
        userId = "user_a",
        email = "user@compa.com",
        companyId = "COMP_A",
        role = UserRole.USER,
        isApproved = true,
        isActive = true
    )

    private val viewerCompA = UserProfile(
        userId = "viewer_a",
        email = "viewer@compa.com",
        companyId = "COMP_A",
        role = UserRole.VIEWER,
        isApproved = true,
        isActive = true
    )

    private val deactivatedAdmin = UserProfile(
        userId = "deactive_admin",
        email = "deactive@compa.com",
        companyId = "COMP_A",
        role = UserRole.ADMIN,
        isApproved = false,
        isActive = false
    )

    @Before
    fun setUp() {
        authorizationManager = AuthorizationManager()
    }

    // --- 1. Company Operations ---
    @Test
    fun testCompanyOperations_OnlySuperUserAllowed() {
        assertTrue(authorizationManager.canCreateCompany(superUser))
        assertFalse(authorizationManager.canCreateCompany(adminCompA))
        assertFalse(authorizationManager.canCreateCompany(userCompA))
        assertFalse(authorizationManager.canCreateCompany(viewerCompA))

        assertTrue(authorizationManager.canUpdateCompany(superUser))
        assertFalse(authorizationManager.canUpdateCompany(adminCompA))

        assertTrue(authorizationManager.canDeleteCompany(superUser))
        assertFalse(authorizationManager.canDeleteCompany(adminCompA))
    }

    // --- 2. User Creation ---
    @Test
    fun testCreateUser_MatrixRules() {
        // Super user can create user in any company
        assertTrue(authorizationManager.canCreateUser(superUser, "COMP_B"))

        // Admin can create user in own company only
        assertTrue(authorizationManager.canCreateUser(adminCompA, "COMP_A"))
        assertFalse(authorizationManager.canCreateUser(adminCompA, "COMP_B"))

        // User & Viewer cannot create users
        assertFalse(authorizationManager.canCreateUser(userCompA, "COMP_A"))
        assertFalse(authorizationManager.canCreateUser(viewerCompA, "COMP_A"))
    }

    // --- 3. Deactivate User ---
    @Test
    fun testDeactivateUser_MatrixRules() {
        // Super user can deactivate non-super user
        assertTrue(authorizationManager.canDeactivateUser(superUser, userCompA))
        assertFalse(authorizationManager.canDeactivateUser(superUser, superUser)) // Cannot deactivate self / super user

        // Admin can deactivate user in own company
        assertTrue(authorizationManager.canDeactivateUser(adminCompA, userCompA))
        assertFalse(authorizationManager.canDeactivateUser(adminCompA, superUser)) // Cannot deactivate super user

        val userCompB = userCompA.copy(userId = "user_b", companyId = "COMP_B")
        assertFalse(authorizationManager.canDeactivateUser(adminCompA, userCompB)) // Cross company blocked
    }

    // --- 4. Change User Role ---
    @Test
    fun testChangeUserRole_MatrixRules() {
        // Super user can assign Admin, User, Viewer
        assertTrue(authorizationManager.canChangeUserRole(superUser, userCompA, UserRole.ADMIN))

        // Admin can assign Admin, User, Viewer in own company
        assertTrue(authorizationManager.canChangeUserRole(adminCompA, userCompA, UserRole.ADMIN))
        assertFalse(authorizationManager.canChangeUserRole(adminCompA, userCompA, UserRole.SUPER_USER)) // Cannot assign SUPER_USER

        // User & Viewer cannot change roles
        assertFalse(authorizationManager.canChangeUserRole(userCompA, viewerCompA, UserRole.ADMIN))
        assertFalse(authorizationManager.canChangeUserRole(viewerCompA, userCompA, UserRole.ADMIN))
    }

    // --- 5. Data Mutations (Create, Update, Delete) ---
    @Test
    fun testDataMutations_MatrixRules() {
        // Create / Update Data: Super, Admin, User -> Allowed; Viewer -> Blocked
        assertTrue(authorizationManager.canCreateData(superUser, "COMP_A"))
        assertTrue(authorizationManager.canCreateData(adminCompA, "COMP_A"))
        assertTrue(authorizationManager.canCreateData(userCompA, "COMP_A"))
        assertFalse(authorizationManager.canCreateData(viewerCompA, "COMP_A"))

        // Delete Data: Super, Admin -> Allowed; User, Viewer -> Blocked
        assertTrue(authorizationManager.canDeleteData(superUser, "COMP_A"))
        assertTrue(authorizationManager.canDeleteData(adminCompA, "COMP_A"))
        assertFalse(authorizationManager.canDeleteData(userCompA, "COMP_A"))
        assertFalse(authorizationManager.canDeleteData(viewerCompA, "COMP_A"))
    }

    // --- 6. Deactivated User Rejection ---
    @Test
    fun testDeactivatedUser_AllActionsDenied() {
        assertFalse(authorizationManager.canCreateCompany(deactivatedAdmin))
        assertFalse(authorizationManager.canCreateUser(deactivatedAdmin, "COMP_A"))
        assertFalse(authorizationManager.canCreateData(deactivatedAdmin, "COMP_A"))
        assertFalse(authorizationManager.canViewData(deactivatedAdmin, "COMP_A"))
    }
}
