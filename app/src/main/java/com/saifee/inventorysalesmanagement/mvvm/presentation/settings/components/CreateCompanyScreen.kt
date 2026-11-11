package com.saifee.inventorysalesmanagement.mvvm.presentation.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.mvvm.presentation.settings.viewmodel.CreateCompanyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCompanyScreen(
    viewModel: CreateCompanyViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    var companyName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage, successMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { AppText("Create Company", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppText(
                text = "Enter Company Details (Email, Phone, Name, and Address are mandatory)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AppTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = "Company Name *",
                leadingIcon = { Icon(Icons.Default.Business, null) },
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Company Email *",
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Company Phone Number *",
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = address,
                onValueChange = { address = it },
                label = "Company Address *",
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = gstNumber,
                onValueChange = { gstNumber = it },
                label = "GST / Tax ID Number",
                leadingIcon = { Icon(Icons.Default.Receipt, null) },
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = website,
                onValueChange = { website = it },
                label = "Website URL",
                leadingIcon = { Icon(Icons.Default.Language, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                text = "Create Company",
                isLoading = isLoading,
                enabled = companyName.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                onClick = {
                    viewModel.createCompany(companyName, email, phone, address, gstNumber, website) {
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
