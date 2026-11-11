package com.saifee.inventorysalesmanagement.mvvm.domain.model

enum class UserRole {
    SUPER_USER,
    ADMIN,
    USER,
    CASHIER,
    VIEWER;

    fun canCreateCompany(): Boolean = this == SUPER_USER
    fun canUpdateCompany(): Boolean = this == SUPER_USER
    fun canDeleteCompany(): Boolean = this == SUPER_USER

    fun canCreateUser(targetCompanyId: String, actorCompanyId: String): Boolean = when (this) {
        SUPER_USER -> true
        ADMIN -> targetCompanyId.isNotEmpty() && targetCompanyId == actorCompanyId
        else -> false
    }

    fun canDeactivateUser(targetCompanyId: String, actorCompanyId: String, targetRole: UserRole): Boolean = when (this) {
        SUPER_USER -> targetRole != SUPER_USER
        ADMIN -> targetCompanyId.isNotEmpty() && targetCompanyId == actorCompanyId && targetRole != SUPER_USER
        else -> false
    }

    fun canChangeRole(targetCompanyId: String, actorCompanyId: String, targetRole: UserRole, newRole: UserRole): Boolean = when (this) {
        SUPER_USER -> targetRole != SUPER_USER && newRole != SUPER_USER // SUPER_USER protection rule
        ADMIN -> targetCompanyId.isNotEmpty() && targetCompanyId == actorCompanyId && 
                targetRole != SUPER_USER && newRole != SUPER_USER
        else -> false
    }

    fun canDeleteData(): Boolean = this == SUPER_USER || this == ADMIN
    fun canModifyData(): Boolean = this == SUPER_USER || this == ADMIN || this == USER || this == CASHIER
    fun canViewData(): Boolean = true
    fun canViewReports(): Boolean = this == SUPER_USER || this == ADMIN || this == USER
    fun canManageSettings(): Boolean = this == SUPER_USER || this == ADMIN

    // Granular RBAC Permissions
    fun canViewCostPrice(): Boolean = this == SUPER_USER || this == ADMIN || this == USER
    fun canViewProfitMargin(): Boolean = this == SUPER_USER || this == ADMIN
    fun canVoidInvoice(): Boolean = this == SUPER_USER || this == ADMIN
    fun canPerformManualStockAdjustment(): Boolean = this == SUPER_USER || this == ADMIN || this == USER
    fun canApplyDiscountWithoutOverride(discountPercent: Double): Boolean =
        (this == SUPER_USER || this == ADMIN) || (discountPercent <= 10.0)
}

enum class UserStatus {
    ACTIVE,
    DEACTIVATED,
    PENDING_APPROVAL
}

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val companyId: String = "",
    val role: UserRole = UserRole.USER,
    val managerPinHash: String = "",
    @get:com.google.firebase.firestore.PropertyName("isApproved")
    @field:com.google.firebase.firestore.PropertyName("isApproved")
    val isApproved: Boolean = false,
    @get:com.google.firebase.firestore.PropertyName("isActive")
    @field:com.google.firebase.firestore.PropertyName("isActive")
    val isActive: Boolean = true,
    val tokenVersion: Long = 1L,
    val featurePermissions: Map<String, Boolean> = mapOf(
        "sales" to true,
        "purchases" to true,
        "inventory" to true,
        "items" to true,
        "customers" to true,
        "vendors" to true,
        "payments" to true,
        "ledger" to true,
        "reports" to true
    ),
    val updatedAt: String = System.currentTimeMillis().toString(),
    val createdAt: String = System.currentTimeMillis().toString()
)

