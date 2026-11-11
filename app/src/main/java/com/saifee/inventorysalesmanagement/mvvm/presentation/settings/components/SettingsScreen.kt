package com.saifee.inventorysalesmanagement.mvvm.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.enums.AppTextStyle
import com.saifee.inventorysalesmanagement.core.enums.AppTheme
import com.saifee.inventorysalesmanagement.core.enums.ButtonType
import com.saifee.inventorysalesmanagement.core.ui.components.*
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.LocationEntity
import com.saifee.inventorysalesmanagement.mvvm.presentation.common.components.DeleteConfirmationDialog
import com.saifee.inventorysalesmanagement.mvvm.presentation.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val themeState by viewModel.themeState.collectAsState()
    val roundOffState by viewModel.roundOffState.collectAsState()
    val companies by viewModel.companies.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val selectedCompanyId by viewModel.selectedCompanyId.collectAsState()
    val selectedLocationId by viewModel.selectedLocationId.collectAsState()

    val businessNameState by viewModel.businessName.collectAsState()
    val taglineState by viewModel.tagline.collectAsState()
    val gstNumberState by viewModel.gstNumber.collectAsState()
    val phoneState by viewModel.phone.collectAsState()
    val emailState by viewModel.email.collectAsState()
    val addressState by viewModel.address.collectAsState()
    val currencySymbolState by viewModel.currencySymbol.collectAsState()
    val defaultTaxRateState by viewModel.defaultTaxRate.collectAsState()
    val invoicePrefixState by viewModel.invoicePrefix.collectAsState()
    val isDeviceFrameMode by viewModel.isDeviceFrameMode.collectAsState()
    val savedSuccess by viewModel.savedSuccess.collectAsState()
    val isAppLockEnabled by viewModel.isAppLockEnabledState.collectAsState()
    val appLockType by viewModel.appLockTypeState.collectAsState()

    SettingsScreenContent(
        themeState = themeState,
        roundOffState = roundOffState,
        companies = companies,
        locations = locations,
        selectedCompanyId = selectedCompanyId,
        selectedLocationId = selectedLocationId,
        businessNameState = businessNameState,
        taglineState = taglineState,
        gstNumberState = gstNumberState,
        phoneState = phoneState,
        emailState = emailState,
        addressState = addressState,
        currencySymbolState = currencySymbolState,
        defaultTaxRateState = defaultTaxRateState,
        invoicePrefixState = invoicePrefixState,
        isDeviceFrameMode = isDeviceFrameMode,
        savedSuccess = savedSuccess,
        isAppLockEnabled = isAppLockEnabled,
        appLockType = appLockType,
        onBack = onBack,
        onThemeSelected = viewModel::setTheme,
        onRoundOffToggle = viewModel::setRoundOff,
        onCompanySelect = viewModel::selectCompany,
        onCompanyDelete = viewModel::deleteCompany,
        onLocationSelect = viewModel::selectLocation,
        onLocationDelete = viewModel::deleteLocation,
        onAddCompany = viewModel::addCompany,
        onAddLocation = { cid, name -> viewModel.addLocation(cid, name) },
        onSaveBusinessSettings = viewModel::updateBusinessSettings,
        onToggleDeviceFrame = viewModel::toggleDeviceFrameMode,
        onChangeOwnPassword = viewModel::changeOwnPassword,
        onAppLockToggle = viewModel::setAppLockEnabled,
        onAppLockTypeChange = viewModel::setAppLockType,
        onSavePin = viewModel::setAppPinCode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    themeState: AppTheme,
    roundOffState: Boolean,
    companies: List<CompanyEntity>,
    locations: List<LocationEntity>,
    selectedCompanyId: String?,
    selectedLocationId: String?,
    businessNameState: String,
    taglineState: String,
    gstNumberState: String,
    phoneState: String,
    emailState: String,
    addressState: String,
    currencySymbolState: String,
    defaultTaxRateState: String,
    invoicePrefixState: String,
    isDeviceFrameMode: Boolean,
    savedSuccess: Boolean,
    isAppLockEnabled: Boolean = false,
    appLockType: String = "BIOMETRIC",
    onBack: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
    onRoundOffToggle: (Boolean) -> Unit,
    onCompanySelect: (String) -> Unit,
    onCompanyDelete: (CompanyEntity) -> Unit,
    onLocationSelect: (String) -> Unit,
    onLocationDelete: (LocationEntity) -> Unit,
    onAddCompany: (String) -> Unit,
    onAddLocation: (String, String) -> Unit,
    onSaveBusinessSettings: (String, String, String, String, String, String, String, String, String) -> Unit,
    onToggleDeviceFrame: () -> Unit,
    onChangeOwnPassword: (currentPass: String, newPass: String, onResult: (Result<Unit>) -> Unit) -> Unit = { _, _, _ -> },
    onAppLockToggle: (Boolean) -> Unit = {},
    onAppLockTypeChange: (String) -> Unit = {},
    onSavePin: (String) -> Unit = {}
) {
    var showAddCompanyDialog by remember { mutableStateOf(false) }
    var showAddLocationDialog by remember { mutableStateOf(false) }
    var companyToDelete by remember { mutableStateOf<CompanyEntity?>(null) }
    var locationToDelete by remember { mutableStateOf<LocationEntity?>(null) }

    var businessName by remember(businessNameState) { mutableStateOf(businessNameState) }
    var tagline by remember(taglineState) { mutableStateOf(taglineState) }
    var gstNumber by remember(gstNumberState) { mutableStateOf(gstNumberState) }
    var phone by remember(phoneState) { mutableStateOf(phoneState) }
    var email by remember(emailState) { mutableStateOf(emailState) }
    var address by remember(addressState) { mutableStateOf(addressState) }
    var currencySymbol by remember(currencySymbolState) { mutableStateOf(currencySymbolState) }
    var defaultTaxRate by remember(defaultTaxRateState) { mutableStateOf(defaultTaxRateState) }
    var invoicePrefix by remember(invoicePrefixState) { mutableStateOf(invoicePrefixState) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Settings",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    AppText(
                        text = "Business Configuration & System Settings",
                        style = AppTextStyle.TITLE,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AppText(
                        text = "Personalize entity profile, tax rules, invoice headers, and UI presentation",
                        style = AppTextStyle.LABEL,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Business Profile & Tax Identification Form
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    AppFormColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppText(
                            text = "Entity Profile & Tax Identification",
                            style = AppTextStyle.TITLE,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        AppTextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            label = "Legal Business Name",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextField(
                            value = tagline,
                            onValueChange = { tagline = it },
                            label = "Commercial Tagline",
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextField(
                            value = gstNumber,
                            onValueChange = { gstNumber = it },
                            label = "GSTIN / Tax ID",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = "Official Phone",
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            AppTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = "Contact Email",
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        AppTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = "Physical Registered Address",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Currency Symbol Selection
                        Column {
                            AppText(
                                text = "Currency Symbol",
                                style = AppTextStyle.LABEL,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("$", "₹", "€", "£", "AED").forEach { sym ->
                                    val isSelected = currencySymbol == sym
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { currencySymbol = sym }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppText(
                                            text = sym,
                                            style = AppTextStyle.BODY,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppTextField(
                                value = defaultTaxRate,
                                onValueChange = { defaultTaxRate = it },
                                label = "Default GST Tax Rate (%)",
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Percent,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            AppTextField(
                                value = invoicePrefix,
                                onValueChange = { invoicePrefix = it },
                                label = "Invoice Prefix Sequence",
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (savedSuccess) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    AppText(
                                        text = "Changes saved!",
                                        style = AppTextStyle.LABEL,
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AppButton(
                                text = "Save Preferences",
                                type = ButtonType.PRIMARY,
                                onClick = {
                                    onSaveBusinessSettings(
                                        businessName,
                                        tagline,
                                        gstNumber,
                                        phone,
                                        email,
                                        address,
                                        currencySymbol,
                                        defaultTaxRate,
                                        invoicePrefix
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Appearance & Billing Section
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppText(
                            text = "Appearance & Billing",
                            style = AppTextStyle.TITLE,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        AppText(
                            text = "Choose your preferred theme and sales options",
                            style = AppTextStyle.LABEL,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        ThemeSelectionSection(
                            currentTheme = themeState,
                            onThemeSelected = onThemeSelected
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                AppText("Enable Round-off", style = AppTextStyle.BODY, fontWeight = FontWeight.SemiBold)
                                AppText(
                                    "Round total amount to nearest rupee by default",
                                    style = AppTextStyle.LABEL,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(checked = roundOffState, onCheckedChange = onRoundOffToggle)
                        }
                    }
                }
            }

            // Security & Change Password Section
            item {
                var currentPass by remember { mutableStateOf("") }
                var newPass by remember { mutableStateOf("") }
                var passStatusMessage by remember { mutableStateOf<String?>(null) }
                var isChangingPass by remember { mutableStateOf(false) }

                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppText(
                            text = "Account Security & Password",
                            style = AppTextStyle.TITLE,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        AppText(
                            text = "Update your login credentials",
                            style = AppTextStyle.LABEL,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        AppPasswordField(
                            value = currentPass,
                            onValueChange = { currentPass = it },
                            label = "Current Password",
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppPasswordField(
                            value = newPass,
                            onValueChange = { newPass = it },
                            label = "New Password",
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (passStatusMessage != null) {
                            AppText(
                                text = passStatusMessage!!,
                                style = AppTextStyle.LABEL,
                                color = if (passStatusMessage!!.contains("success", ignoreCase = true)) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            AppButton(
                                text = "Change Password",
                                isLoading = isChangingPass,
                                enabled = currentPass.isNotBlank() && newPass.length >= 6,
                                onClick = {
                                    isChangingPass = true
                                    passStatusMessage = null
                                    onChangeOwnPassword(currentPass, newPass) { res ->
                                        isChangingPass = false
                                        res.fold(
                                            onSuccess = {
                                                passStatusMessage = "Password updated successfully!"
                                                currentPass = ""
                                                newPass = ""
                                            },
                                            onFailure = { err ->
                                                passStatusMessage = err.localizedMessage ?: "Failed to change password."
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // App Lock & Biometrics Section
            item {
                var showSetPinDialog by remember { mutableStateOf(false) }
                var newPinInput by remember { mutableStateOf("") }
                var pinSavedSuccess by remember { mutableStateOf(false) }

                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppText(
                            text = "App Lock & Biometrics",
                            style = AppTextStyle.TITLE,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        AppText(
                            text = "Secure app access using Biometric unlock or PIN",
                            style = AppTextStyle.LABEL,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                AppText("Enable App Lock", style = AppTextStyle.BODY, fontWeight = FontWeight.SemiBold)
                                AppText("Require unlock when opening or resuming app", style = AppTextStyle.LABEL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isAppLockEnabled,
                                onCheckedChange = onAppLockToggle
                            )
                        }

                        if (isAppLockEnabled) {
                            HorizontalDivider()

                            AppText("Authentication Method", style = AppTextStyle.BODY, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FilterChip(
                                    selected = appLockType == "BIOMETRIC",
                                    onClick = { onAppLockTypeChange("BIOMETRIC") },
                                    label = { AppText("Biometric / Face") }
                                )
                                FilterChip(
                                    selected = appLockType == "PIN",
                                    onClick = { onAppLockTypeChange("PIN") },
                                    label = { AppText("4-Digit PIN") }
                                )
                            }

                            if (appLockType == "PIN") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppText("Set or Change PIN Code", style = AppTextStyle.BODY)
                                    AppButton(
                                        text = "Configure PIN",
                                        onClick = { showSetPinDialog = true }
                                    )
                                }
                                if (pinSavedSuccess) {
                                    AppText("PIN saved successfully!", color = Color(0xFF4CAF50), style = AppTextStyle.LABEL)
                                }
                            }
                        }
                    }
                }

                if (showSetPinDialog) {
                    AlertDialog(
                        onDismissRequest = { showSetPinDialog = false },
                        title = { AppText("Set 4-Digit App PIN") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                AppPasswordField(
                                    value = newPinInput,
                                    onValueChange = { if (it.length <= 4) newPinInput = it },
                                    label = "Enter 4-Digit PIN"
                                )
                            }
                        },
                        confirmButton = {
                            AppButton(
                                text = "Save PIN",
                                enabled = newPinInput.length == 4,
                                onClick = {
                                    onSavePin(newPinInput)
                                    showSetPinDialog = false
                                    pinSavedSuccess = true
                                    newPinInput = ""
                                }
                            )
                        },
                        dismissButton = {
                            TextButton(onClick = { showSetPinDialog = false }) {
                                AppText("Cancel")
                            }
                        }
                    )
                }
            }

            // Companies & Locations
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OrganizationHeader(
                            title = "Companies",
                            onAddClick = { showAddCompanyDialog = true }
                        )

                        if (companies.isEmpty()) {
                            EmptySectionText("No companies added yet.")
                        } else {
                            companies.forEach { company ->
                                EntityItem(
                                    name = company.companyName,
                                    isSelected = company.companyId == selectedCompanyId,
                                    icon = Icons.Default.Business,
                                    onSelect = { onCompanySelect(company.companyId) },
                                    onDelete = { companyToDelete = company }
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        OrganizationHeader(
                            title = "Locations",
                            onAddClick = {
                                if (selectedCompanyId != null) {
                                    showAddLocationDialog = true
                                }
                            },
                            enabled = selectedCompanyId != null
                        )

                        if (selectedCompanyId == null) {
                            EmptySectionText("Select a company to view locations.")
                        } else if (locations.isEmpty()) {
                            EmptySectionText("No locations added for this company.")
                        } else {
                            locations.forEach { location ->
                                EntityItem(
                                    name = location.locationName,
                                    isSelected = location.locationId == selectedLocationId,
                                    icon = Icons.Default.LocationOn,
                                    onSelect = { onLocationSelect(location.locationId) },
                                    onDelete = { locationToDelete = location }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCompanyDialog) {
        AddEntityBottomSheet(
            title = "Add Company",
            onDismiss = { showAddCompanyDialog = false },
            onConfirm = { name ->
                onAddCompany(name)
                showAddCompanyDialog = false
            }
        )
    }

    if (showAddLocationDialog) {
        AddEntityBottomSheet(
            title = "Add Location",
            onDismiss = { showAddLocationDialog = false },
            onConfirm = { name ->
                selectedCompanyId?.let { onAddLocation(it, name) }
                showAddLocationDialog = false
            }
        )
    }

    companyToDelete?.let { company ->
        DeleteConfirmationDialog(
            onConfirm = {
                onCompanyDelete(company)
                companyToDelete = null
            },
            onDismiss = { companyToDelete = null },
            message = "Are you sure you want to delete company '${company.companyName}'?"
        )
    }

    locationToDelete?.let { location ->
        DeleteConfirmationDialog(
            onConfirm = {
                onLocationDelete(location)
                locationToDelete = null
            },
            onDismiss = { locationToDelete = null },
            message = "Are you sure you want to delete location '${location.locationName}'?"
        )
    }
}

@Composable
fun OrganizationHeader(
    title: String,
    onAddClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppText(
            text = title,
            style = AppTextStyle.TITLE,
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onAddClick, enabled = enabled) {
            Icon(Icons.Default.Add, contentDescription = "Add $title")
        }
    }
}

@Composable
fun EntityItem(
    name: String,
    isSelected: Boolean,
    icon: ImageVector,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            AppText(
                text = name,
                modifier = Modifier.weight(1f),
                style = AppTextStyle.BODY,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                RadioButton(selected = true, onClick = null)
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmptySectionText(text: String) {
    AppText(
        text = text,
        style = AppTextStyle.BODY,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntityBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppText(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            AppButton(
                text = "Add",
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ThemeSelectionSection(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    val options = listOf(
        AppTheme.LIGHT to "Light Mode",
        AppTheme.DARK to "Dark Mode",
        AppTheme.SYSTEM to "System Default"
    )

    Column(Modifier.selectableGroup()) {
        options.forEach { (theme, label) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (theme == currentTheme),
                        onClick = { onThemeSelected(theme) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (theme == currentTheme),
                    onClick = null
                )
                Spacer(Modifier.width(16.dp))
                AppText(text = label, style = AppTextStyle.BODY)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    InventorySalesManagementTheme {
        SettingsScreenContent(
            themeState = AppTheme.SYSTEM,
            roundOffState = true,
            companies = listOf(
                CompanyEntity(
                    companyId = "C1",
                    companyName = "Saifee Inventory",
                    createdAt = 0L,
                    updatedAt = 0L
                )
            ),
            locations = listOf(),
            selectedCompanyId = "C1",
            selectedLocationId = "L1",
            businessNameState = "Saifee Inventory",
            taglineState = "Quality Inventory Management",
            gstNumberState = "27AAAAA0000A1Z5",
            phoneState = "+91 9876543210",
            emailState = "info@saifee.com",
            addressState = "123 Commercial Street, Mumbai",
            currencySymbolState = "₹",
            defaultTaxRateState = "18.0",
            invoicePrefixState = "INV-",
            isDeviceFrameMode = false,
            savedSuccess = false,
            onBack = {},
            onThemeSelected = {},
            onRoundOffToggle = {},
            onCompanySelect = {},
            onCompanyDelete = {},
            onLocationSelect = {},
            onLocationDelete = {},
            onAddCompany = {},
            onAddLocation = { _, _ -> },
            onSaveBusinessSettings = { _, _, _, _, _, _, _, _, _ -> },
            onToggleDeviceFrame = {}
        )
    }
}
