package com.saifee.inventorysalesmanagement.mvvm.presentation.signup.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saifee.inventorysalesmanagement.core.adaptive.LocalAdaptiveContext
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.enums.AppTextStyle
import com.saifee.inventorysalesmanagement.core.ui.components.AppAnimatedVisibility
import com.saifee.inventorysalesmanagement.core.ui.components.AppButton
import com.saifee.inventorysalesmanagement.core.ui.components.AppFormColumn
import com.saifee.inventorysalesmanagement.core.ui.components.AppPasswordField
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.components.AppTextField
import com.saifee.inventorysalesmanagement.mvvm.presentation.signup.viewmodel.SignupViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    onLoginClick: () -> Unit = {},
    onSignupSuccess: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf(viewModel.initialCompanyId) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

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
        val logoSize = if (adaptiveContext.isSmallScreen) 80.dp else 110.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppTokens.Spacing.PagePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (adaptiveContext.isSmallScreen) 24.dp else 40.dp))

            AnimatedLogo(visible, logoSize)

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            AppAnimatedVisibility(visible = visible, duration = 600) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppText(
                        "Create Account",
                        style = AppTextStyle.HEADLINE,
                        fontWeight = FontWeight.Bold
                    )
                    AppText("Sign up to continue (Requires Admin approval)", style = AppTextStyle.LABEL)
                }
            }

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Large))

            AppFormColumn {
                AppAnimatedVisibility(visible, delay = 100) {
                    AppTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        singleLine = true
                    )
                }

                AppAnimatedVisibility(visible, delay = 150) {
                    AppTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Mobile Number",
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        singleLine = true
                    )
                }

                AppAnimatedVisibility(visible, delay = 200) {
                    AppTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = "Company ID / Name",
                        leadingIcon = { Icon(Icons.Default.Inventory2, null) },
                        singleLine = true
                    )
                }

                AppAnimatedVisibility(visible, delay = 300) {
                    AppTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        singleLine = true
                    )
                }

                AppAnimatedVisibility(visible, delay = 400) {
                    AppPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password"
                    )
                }

                AppAnimatedVisibility(visible, delay = 500) {
                    AppPasswordField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = "Confirm Password",
                        onDone = {
                            if (password == confirm) {
                                viewModel.register(email, password, name, company, phone, onSignupSuccess)
                            }
                        }
                    )
                }

                AppAnimatedVisibility(visible, delay = 600) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it }
                        )
                        AppText(text = "I agree to Terms & Conditions", style = AppTextStyle.LABEL)
                    }
                }

                AppAnimatedVisibility(visible, delay = 700) {
                    AppButton(
                        text = "Create Account",
                        isLoading = isLoading,
                        enabled = termsAccepted,
                        onClick = {
                            if (password == confirm) {
                                viewModel.register(email, password, name, company, phone, onSignupSuccess)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

            AppAnimatedVisibility(visible, delay = 800) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppText("Already have an account? ")
                    AppText(
                        text = "Login",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (adaptiveContext.isSmallScreen) 32.dp else 56.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun AnimatedLogo(visible: Boolean, size: Dp) {
    val infinite = rememberInfiniteTransition(label = "")
    val offset by infinite.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    AppAnimatedVisibility(visible = visible, duration = 800) {
        Surface(
            modifier = Modifier
                .offset(y = offset.dp)
                .size(size)
                .shadow(AppTokens.Elevation.High, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    MaterialTheme {
        SignupScreen()
    }
}
