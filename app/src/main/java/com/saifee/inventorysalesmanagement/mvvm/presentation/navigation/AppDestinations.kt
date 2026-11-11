package com.saifee.inventorysalesmanagement.mvvm.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val showInNavSuite: Boolean = true,
    val showInDrawer: Boolean = true
) {
    SPLASH("splash", "Splash", Icons.Default.Home, false, false),
    LOGIN("login", "Login", Icons.Default.Home, false, false),
    COMPANY_CONFIG("company_config", "Company Config", Icons.Default.Warehouse, false, false),
    SIGNUP("signup?companyId={companyId}", "Signup", Icons.Default.Home, false, false),
    CREATE_COMPANY("create_company", "Create Company", Icons.Default.Business, false, true),
    DASHBOARD("dashboard", "Dashboard", Icons.Default.Home, true, true),
    ITEMS("items", "Items", Icons.Default.Inventory, true, true),
    INVENTORY("inventory", "Inventory", Icons.Default.Warehouse, false, true),
    ITEM_GROUP("item_group", "Item Groups", Icons.Default.Category, false, true),
    SALES("sales", "Sales", Icons.AutoMirrored.Filled.ReceiptLong, false, true),
    PURCHASE("purchase", "Purchase", Icons.Default.ShoppingBag, false, true),
    CUSTOMERS("customers?customerId={customerId}", "Customers", Icons.Default.Groups, false, true),
    VENDORS("vendors?vendorId={vendorId}", "Vendors", Icons.Default.Groups, false, true),
    PAYMENTS("payments", "Payments", Icons.Default.AccountBalanceWallet, false, true),
    LEDGER("ledger", "Party Ledger", Icons.AutoMirrored.Filled.ReceiptLong, false, true),
    REPORTS("reports", "Reports", Icons.Default.Assessment, false, true),
    ADD_ITEM("add_item?itemId={itemId}", "Add", Icons.Default.Add, false, false),
    PRODUCT_DETAILS("product_details?itemId={itemId}", "Product Details", Icons.Default.Inventory, false, false),
    CREATE_SALE("create_sale?salesId={salesId}", "New Sale", Icons.Default.Add, false, false),
    CREATE_PURCHASE("create_purchase", "New Purchase", Icons.Default.Add, false, false),
    INVOICE_PREVIEW("invoice_preview?salesId={salesId}", "Invoice", Icons.Default.Assessment, false, false),
    USER_MANAGEMENT("user_management", "User Management", Icons.Default.Groups, false, true),
    SYNC("sync", "Sync & Backup", Icons.Default.CloudSync, false, true),
    SETTINGS("settings", "Settings", Icons.Default.Settings, false, true)
}
