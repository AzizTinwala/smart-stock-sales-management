package com.saifee.inventorysalesmanagement.mvvm.presentation.main.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saifee.inventorysalesmanagement.core.adaptive.LocalAdaptiveContext
import com.saifee.inventorysalesmanagement.core.enums.NavigationMode
import com.saifee.inventorysalesmanagement.mvvm.presentation.customers.components.CustomersScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.components.DashboardScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.dashboard.viewmodel.DashboardViewModel
import com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components.InventoryScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.viewmodel.InventoryViewModel
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.components.AddItemScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.components.ItemGroupScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.components.ItemsScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.login.components.LoginScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.navigation.AppDestinations
import com.saifee.inventorysalesmanagement.mvvm.presentation.payments.components.PaymentsScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.ledger.PartyLedgerScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.components.CreatePurchaseScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.purchase.components.PurchaseScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.reports.components.ReportsScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.settings.components.CreateCompanyScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.signup.components.CompanyConfigScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.reports.viewmodel.ReportsViewModel
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.components.CreateSaleScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.components.InvoicePreviewScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.components.SalesScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.settings.components.SettingsScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.settings.components.SyncScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.settings.viewmodel.SettingsViewModel
import com.saifee.inventorysalesmanagement.mvvm.presentation.signup.components.SignupScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.splashscreen.components.SplashScreen
import com.saifee.inventorysalesmanagement.mvvm.presentation.vendors.components.VendorsScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saifee.inventorysalesmanagement.core.ui.components.AppBottomBar
import com.saifee.inventorysalesmanagement.core.ui.components.AppDrawer
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.StockPilotMoreBottomSheet
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.presentation.item.components.ProductDetailsScreen
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private val reportsViewModel: ReportsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState by settingsViewModel.themeState.collectAsState()
            InventorySalesManagementTheme(
                appTheme = themeState
            ) {
                InventorySalesManagementApp(
                    settingsViewModel,
                    dashboardViewModel,
                    inventoryViewModel,
                    reportsViewModel
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun InventorySalesManagementApp(
    settingsViewModel: SettingsViewModel,
    dashboardViewModel: DashboardViewModel,
    inventoryViewModel: InventoryViewModel,
    reportsViewModel: ReportsViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val adaptiveContext = LocalAdaptiveContext.current
    val context = LocalContext.current
    val sharedPref = remember { SharedPref(context) }
    var isLocked by remember {
        mutableStateOf(sharedPref.isAppLockEnabled && !sharedPref.userId.isNullOrEmpty())
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner, sharedPref.isAppLockEnabled) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) {
                if (sharedPref.isAppLockEnabled && !sharedPref.userId.isNullOrEmpty()) {
                    isLocked = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val isSplashOrAuth = currentRoute in listOf(
        AppDestinations.SPLASH.route,
        AppDestinations.LOGIN.route,
        AppDestinations.COMPANY_CONFIG.route,
        AppDestinations.CREATE_COMPANY.route,
        AppDestinations.SETTINGS.route,
        null
    ) || currentRoute.orEmpty().startsWith("signup")

    val needsCompanyAndLocation = !isSplashOrAuth &&
            !sharedPref.userId.isNullOrEmpty() &&
            (sharedPref.selectedCompanyId.isNullOrEmpty() || sharedPref.selectedLocationId.isNullOrEmpty())

    androidx.compose.runtime.LaunchedEffect(currentRoute, sharedPref.selectedCompanyId, sharedPref.selectedLocationId) {
        if (needsCompanyAndLocation) {
            navController.navigate(AppDestinations.SETTINGS.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    var showMoreBottomSheet by remember { mutableStateOf(false) }

    val showNavigation = currentRoute != null && currentRoute !in listOf(
        AppDestinations.SPLASH.route,
        AppDestinations.LOGIN.route,
        AppDestinations.SIGNUP.route
    )

    val needsLock = sharedPref.isAppLockEnabled &&
            !sharedPref.userId.isNullOrEmpty() &&
            currentRoute !in listOf(
                AppDestinations.SPLASH.route,
                AppDestinations.LOGIN.route,
                AppDestinations.COMPANY_CONFIG.route,
                null
            ) && !currentRoute.orEmpty().startsWith("signup")

    Box(modifier = Modifier.fillMaxSize()) {
        if (showNavigation) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = adaptiveContext.navigationMode == NavigationMode.DRAWER,
                drawerContent = {
                    AppDrawer(
                        navController = navController,
                        drawerState = drawerState
                    )
                }
            ) {
                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        AppBottomBar(
                            currentRoute = currentRoute,
                            onNavigateTo = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            onMoreClick = { showMoreBottomSheet = true }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavHost(
                            navController = navController,
                            drawerState = drawerState,
                            scope = scope,
                            settingsViewModel = settingsViewModel,
                            dashboardViewModel = dashboardViewModel,
                            inventoryViewModel = inventoryViewModel,
                            reportsViewModel = reportsViewModel
                        )
                    }
                }
            }

            if (showMoreBottomSheet) {
                StockPilotMoreBottomSheet(
                    onDismissRequest = { showMoreBottomSheet = false },
                    onNavigateTo = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
                AppNavHost(
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope,
                    settingsViewModel = settingsViewModel,
                    dashboardViewModel = dashboardViewModel,
                    inventoryViewModel = inventoryViewModel,
                    reportsViewModel = reportsViewModel
                )
            }
        }

        if (needsLock && isLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                com.saifee.inventorysalesmanagement.mvvm.presentation.lock.components.AppLockScreen(
                    onUnlocked = { isLocked = false },
                    onLogout = {
                        isLocked = false
                        navController.navigate(AppDestinations.LOGIN.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    settingsViewModel: SettingsViewModel,
    dashboardViewModel: DashboardViewModel,
    inventoryViewModel: InventoryViewModel,
    reportsViewModel: ReportsViewModel
) {
    AppNavHostContent(
        navController = navController,
        drawerState = drawerState,
        scope = scope,
        splashScreen = {
            SplashScreen(navController = navController)
        },
        loginScreen = {
            LoginScreen(
                onLogin = {
                    navController.navigate(AppDestinations.DASHBOARD.route) {
                        popUpTo(AppDestinations.LOGIN.route) { inclusive = true }
                    }
                },
                onSignUp = {
                    navController.navigate(AppDestinations.COMPANY_CONFIG.route)
                }
            )
        },
        companyConfigScreen = {
            CompanyConfigScreen(
                onContinue = { companyId ->
                    navController.navigate("signup?companyId=$companyId")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        },
        signupScreen = {
            SignupScreen(
                onLoginClick = {
                    navController.popBackStack()
                },
                onSignupSuccess = {
                    navController.popBackStack()
                }
            )
        },
        dashboardScreen = {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onMenuClick = { scope.launch { drawerState.open() } },
                onFabClick = { navController.navigate(AppDestinations.ADD_ITEM.route) },
                onNavigateTo = { route -> navController.navigate(route) },
                onScanBarcodeClick = { navController.navigate(AppDestinations.ITEMS.route) }
            )
        },
        inventoryScreen = {
            InventoryScreen(
                viewModel = inventoryViewModel,
                onMenuClick = { scope.launch { drawerState.open() } },
                onAddClick = { navController.navigate(AppDestinations.ADD_ITEM.route) }
            )
        },
        reportsScreen = {
            ReportsScreen(
                viewModel = reportsViewModel,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },
        settingsScreen = {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        },
        createCompanyScreen = {
            CreateCompanyScreen(
                onBack = { navController.popBackStack() }
            )
        }
    )
}

@Composable
fun AppNavHostContent(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    splashScreen: @Composable () -> Unit,
    loginScreen: @Composable () -> Unit,
    companyConfigScreen: @Composable () -> Unit,
    signupScreen: @Composable () -> Unit,
    createCompanyScreen: @Composable () -> Unit,
    dashboardScreen: @Composable () -> Unit,
    inventoryScreen: @Composable () -> Unit,
    reportsScreen: @Composable () -> Unit,
    settingsScreen: @Composable () -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(AppDestinations.SPLASH.route) {
            splashScreen()
        }

        composable(AppDestinations.LOGIN.route) {
            loginScreen()
        }

        composable(AppDestinations.COMPANY_CONFIG.route) {
            companyConfigScreen()
        }

        composable(AppDestinations.CREATE_COMPANY.route) {
            createCompanyScreen()
        }

        composable(
            route = AppDestinations.SIGNUP.route,
            arguments = listOf(navArgument("companyId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            signupScreen()
        }

        composable(AppDestinations.DASHBOARD.route) {
            dashboardScreen()
        }

        composable(
            route = AppDestinations.ITEMS.route + "?groupId={groupId}",
            arguments = listOf(navArgument("groupId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: -1L
            ItemsScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                onMenuClick = { scope.launch { drawerState.open() } },
                initialGroupId = if (groupId == -1L) null else groupId
            )
        }

        composable(
            route = "product_details?itemId={itemId}",
            arguments = listOf(navArgument("itemId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: -1L
            val inventoryViewModel: InventoryViewModel = hiltViewModel()
            val items by inventoryViewModel.items.collectAsStateWithLifecycle()
            val selectedItem = items.find { it.itemId == itemId } ?: ItemEntity(
                itemId = itemId,
                companyId = "",
                locationId = "",
                itemName = "Industrial Valve 4-inch",
                groupId = 1L,
                sku = "SKU-IV-04",
                barcode = "BAR-IV-04",
                purchasePrice = 25.0,
                sellingPrice = 45.0,
                stockQty = 100.0,
                unit = "pcs",
                lowStockLevel = 15.0,
                criticalStockLevel = 5.0,
                createdAt = 0L,
                updatedAt = 0L
            )
            ProductDetailsScreen(
                item = selectedItem,
                onBack = { navController.popBackStack() },
                onEdit = { item -> navController.navigate("add_item?itemId=${item.itemId}") },
                onStockIn = { navController.navigate(AppDestinations.PURCHASE.route) },
                onStockOut = { navController.navigate(AppDestinations.SALES.route) }
            )
        }

        composable(AppDestinations.INVENTORY.route) {
            inventoryScreen()
        }

        composable(AppDestinations.ITEM_GROUP.route) {
            ItemGroupScreen(
                viewModel = hiltViewModel(),
                onMenuClick = { scope.launch { drawerState.open() } },
                onProfileClick = { /* TODO */ },

                onGroupClick = { group ->
                    navController.navigate("items?groupId=${group.groupId}")
                }
            )
        }

        composable(AppDestinations.SALES.route) {
            SalesScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }

        composable(
            route = AppDestinations.CREATE_SALE.route,
            arguments = listOf(navArgument("salesId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            CreateSaleScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.PURCHASE.route) {
            PurchaseScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }

        composable(
            route = AppDestinations.CREATE_PURCHASE.route,
            arguments = listOf(navArgument("purchaseId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            CreatePurchaseScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestinations.CUSTOMERS.route,
            arguments = listOf(navArgument("customerId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            CustomersScreen(
                viewModel = hiltViewModel(),
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }

        composable(
            route = AppDestinations.VENDORS.route,
            arguments = listOf(navArgument("vendorId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            VendorsScreen(
                viewModel = hiltViewModel(),
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }

        composable(AppDestinations.REPORTS.route) {
            reportsScreen()
        }

        composable(
            route = AppDestinations.INVOICE_PREVIEW.route,
            arguments = listOf(navArgument("salesId") { type = NavType.StringType })
        ) {
            InvoicePreviewScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.PAYMENTS.route) {
            PaymentsScreen(
                viewModel = hiltViewModel(),
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }

        composable(AppDestinations.LEDGER.route) {
            PartyLedgerScreen(
                viewModel = hiltViewModel(),
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }

        composable(
            route = AppDestinations.ADD_ITEM.route,
            arguments = listOf(navArgument("itemId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) {
            AddItemScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }


        composable(AppDestinations.USER_MANAGEMENT.route) {
            com.saifee.inventorysalesmanagement.mvvm.presentation.users.components.UserManagementScreen(
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        }

        composable(AppDestinations.SYNC.route) {
            SyncScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.SETTINGS.route) {
            settingsScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavHostPreview() {
    InventorySalesManagementTheme {
        AppNavHostContent(
            navController = rememberNavController(),
            drawerState = rememberDrawerState(DrawerValue.Closed),
            scope = rememberCoroutineScope(),
            splashScreen = { SplashScreen(navController = rememberNavController()) },
            loginScreen = { androidx.compose.material3.Text("Login Screen") },
            companyConfigScreen = { androidx.compose.material3.Text("Company Config Screen") },
            signupScreen = { androidx.compose.material3.Text("Signup Screen") },
            createCompanyScreen = { androidx.compose.material3.Text("Create Company Screen") },
            dashboardScreen = { androidx.compose.material3.Text("Dashboard Screen") },
            inventoryScreen = { androidx.compose.material3.Text("Inventory Screen") },
            reportsScreen = { androidx.compose.material3.Text("Reports Screen") },
            settingsScreen = { androidx.compose.material3.Text("Settings Screen") }
        )
    }
}
