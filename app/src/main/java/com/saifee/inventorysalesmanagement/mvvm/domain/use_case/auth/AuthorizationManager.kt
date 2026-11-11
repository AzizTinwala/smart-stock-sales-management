package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth

import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserProfile
import com.saifee.inventorysalesmanagement.mvvm.domain.model.UserRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationManager @Inject constructor() {

    private fun isUserValid(actor: UserProfile?): Boolean {
        if (actor == null) return false
        if (actor.role == UserRole.SUPER_USER) return true
        return actor.isApproved && actor.isActive
    }

    // 1. Company Operations
    fun canCreateCompany(actor: UserProfile?): Boolean {
        if (!isUserValid(actor)) return false
        return actor!!.role.canCreateCompany()
    }

    fun canUpdateCompany(actor: UserProfile?): Boolean {
        if (!isUserValid(actor)) return false
        return actor!!.role.canUpdateCompany()
    }

    fun canDeleteCompany(actor: UserProfile?): Boolean {
        if (!isUserValid(actor)) return false
        return actor!!.role.canDeleteCompany()
    }

    // 2. User Operations
    fun canCreateUser(actor: UserProfile?, targetCompanyId: String): Boolean {
        if (!isUserValid(actor)) return false
        val actorProfile = actor!!
        return actorProfile.role.canCreateUser(targetCompanyId, actorProfile.companyId)
    }

    fun canDeactivateUser(actor: UserProfile?, targetUser: UserProfile): Boolean {
        if (!isUserValid(actor)) return false
        val actorProfile = actor!!
        // Prevent self deactivation/modification
        if (actorProfile.userId == targetUser.userId) return false
        return actorProfile.role.canDeactivateUser(targetUser.companyId, actorProfile.companyId, targetUser.role)
    }

    fun canChangeUserRole(actor: UserProfile?, targetUser: UserProfile, newRole: UserRole): Boolean {
        if (!isUserValid(actor)) return false
        val actorProfile = actor!!
        // Prevent self privilege escalation / self role modification
        if (actorProfile.userId == targetUser.userId) return false
        return actorProfile.role.canChangeRole(targetUser.companyId, actorProfile.companyId, targetUser.role, newRole)
    }

    // 3. Data Isolation & Access
    fun canViewData(actor: UserProfile?, targetCompanyId: String): Boolean {
        if (!isUserValid(actor)) return false
        val actorProfile = actor!!
        if (actorProfile.role == UserRole.SUPER_USER) return true
        if (targetCompanyId.isEmpty()) return actorProfile.companyId.isNotEmpty()
        return actorProfile.companyId == targetCompanyId
    }

    fun canCreateData(actor: UserProfile?, targetCompanyId: String): Boolean {
        if (!canViewData(actor, targetCompanyId)) return false
        val actorProfile = actor!!
        return actorProfile.role.canModifyData()
    }

    fun canUpdateData(actor: UserProfile?, targetCompanyId: String): Boolean {
        if (!canViewData(actor, targetCompanyId)) return false
        val actorProfile = actor!!
        return actorProfile.role.canModifyData()
    }

    fun canDeleteData(actor: UserProfile?, targetCompanyId: String): Boolean {
        if (!canViewData(actor, targetCompanyId)) return false
        val actorProfile = actor!!
        return actorProfile.role.canDeleteData()
    }

    // 4. Reports & Settings
    fun canViewReports(actor: UserProfile?, targetCompanyId: String): Boolean {
        if (!canViewData(actor, targetCompanyId)) return false
        val actorProfile = actor!!
        return actorProfile.role.canViewReports()
    }

    fun canManageSettings(actor: UserProfile?, targetCompanyId: String): Boolean {
        if (!canViewData(actor, targetCompanyId)) return false
        val actorProfile = actor!!
        return actorProfile.role.canManageSettings()
    }

    // 5. Granular Financial & Operational Security
    fun canViewCostPrice(actor: UserProfile?): Boolean {
        if (!isUserValid(actor)) return false
        return actor!!.role.canViewCostPrice()
    }

    fun canViewProfitMargin(actor: UserProfile?): Boolean {
        if (!isUserValid(actor)) return false
        return actor!!.role.canViewProfitMargin()
    }

    fun canVoidInvoice(actor: UserProfile?): Boolean {
        if (!isUserValid(actor)) return false
        return actor!!.role.canVoidInvoice()
    }

    fun requiresManagerOverrideForDiscount(actor: UserProfile?, discountPercent: Double): Boolean {
        if (!isUserValid(actor)) return true
        return !actor!!.role.canApplyDiscountWithoutOverride(discountPercent)
    }

    fun requiresManagerOverrideForStockAdjustment(actor: UserProfile?): Boolean {
        if (!isUserValid(actor)) return true
        return !actor!!.role.canPerformManualStockAdjustment()
    }

    // Legacy helper methods for backwards compatibility
    fun canAccessFeature(profile: UserProfile?, featureKey: String): Boolean {
        if (!isUserValid(profile)) return false
        val actor = profile!!
        if (actor.role == UserRole.SUPER_USER || actor.role == UserRole.ADMIN) return true
        return actor.featurePermissions[featureKey] ?: true
    }

    fun canManageUsers(actorProfile: UserProfile?, targetCompanyId: String): Boolean {
        return canCreateUser(actorProfile, targetCompanyId)
    }

    fun canManageCompany(actorProfile: UserProfile?): Boolean {
        return canCreateCompany(actorProfile)
    }
}

