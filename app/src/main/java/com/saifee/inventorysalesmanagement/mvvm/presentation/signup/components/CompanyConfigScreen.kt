package com.saifee.inventorysalesmanagement.mvvm.presentation.signup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saifee.inventorysalesmanagement.core.adaptive.LocalAdaptiveContext
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.enums.AppTextStyle
import com.saifee.inventorysalesmanagement.core.ui.components.AppAnimatedVisibility
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppFormColumn
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.mvvm.presentation.signup.viewmodel.CompanyConfigViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CompanyConfigScreen(
    viewModel: CompanyConfigViewModel = hiltViewModel(),
    onContinue: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    val companyId by viewModel.companyId.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    val adaptiveContext = LocalAdaptiveContext.current

    LaunchedEffect(Unit) {
        delay(150.milliseconds)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF6F9FC), Color.White)
                )
            )
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppTokens.Spacing.PagePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (adaptiveContext.isSmallScreen) 32.dp else 60.dp))

            AppAnimatedVisibility(visible = visible, duration = 600) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppText(
                        "Company Configuration",
                        style = AppTextStyle.HEADLINE,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppText("Enter your Company ID or Code before registering", style = AppTextStyle.LABEL)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AppFormColumn {
                AppAnimatedVisibility(visible, delay = 100) {
                    AppTextField(
                        value = companyId,
                        onValueChange = { viewModel.onCompanyIdChanged(it) },
                        label = "Company ID / Code",
                        leadingIcon = { Icon(Icons.Default.Business, null) },
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppAnimatedVisibility(visible, delay = 200) {
                    AppButton(
                        text = "Continue to Registration",
                        onClick = {
                            viewModel.validateAndProceed { cid ->
                                onContinue(cid)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AppAnimatedVisibility(visible, delay = 300) {
                    TextButton(onClick = onBack) {
                        AppText("Back to Login", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
