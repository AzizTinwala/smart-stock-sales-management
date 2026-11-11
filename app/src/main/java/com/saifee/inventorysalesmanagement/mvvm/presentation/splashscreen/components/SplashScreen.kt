package com.saifee.inventorysalesmanagement.mvvm.presentation.splashscreen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppText
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.core.ui.theme.LightSecondaryContainer
import com.saifee.inventorysalesmanagement.mvvm.presentation.navigation.AppDestinations
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        delay(2000)
        val prefs = com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref(context)
        val userId = prefs.userId

        val destination = if (!userId.isNullOrEmpty()) {
            AppDestinations.DASHBOARD.route
        } else {
            AppDestinations.LOGIN.route
        }

        navController.navigate(destination) {
            popUpTo(AppDestinations.SPLASH.route) { inclusive = true }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        val isSmallScreen = screenHeight < 600.dp

        val logoSize = (screenHeight * 0.2f).coerceIn(100.dp, 160.dp)
        val titleFontSize = if (isSmallScreen) 42.sp else 52.sp
        val subtitleFontSize = if (isSmallScreen) 14.sp else 18.sp

        // Grid Background
        GridBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))

            // Logo Card
            AppCard(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(AppTokens.Elevation.High),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(logoSize)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = MaterialTheme.colorScheme.primary,
                        spotColor = MaterialTheme.colorScheme.primary
                    )
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(logoSize * 0.4f)
                    )

                }
            }

            Spacer(modifier = Modifier.height(if (isSmallScreen) 24.dp else 42.dp))

            AppText(
                text = "SAIFEE",
                fontFamily = FontFamily.SansSerif,
                fontSize = titleFontSize,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(AppTokens.Spacing.Small))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                HorizontalDivider(
                    modifier = Modifier.width(if (isSmallScreen) 30.dp else 40.dp),
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )

                AppText(
                    modifier = Modifier.padding(horizontal = AppTokens.Spacing.Medium),
                    text = "INVENTORY",
                    fontSize = subtitleFontSize,
                    letterSpacing = if (isSmallScreen) 4.sp else 6.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(
                    modifier = Modifier.width(if (isSmallScreen) 30.dp else 40.dp),
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )

            }

            Spacer(modifier = Modifier.weight(1f))

            LinearProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier
                    .width(screenWidth * 0.6f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            Spacer(modifier = Modifier.height(if (isSmallScreen) 30.dp else 60.dp))

            AppText(
                text = "THE PRECISE MONOLITH",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = if (isSmallScreen) 2.sp else 3.sp,
                fontSize = if (isSmallScreen) 12.sp else 15.sp
            )

            Spacer(modifier = Modifier.height(if (isSmallScreen) 32.dp else 56.dp))

        }
    }
}

@Composable
fun GridBackground() {

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        val spacing = 90f

        var x = 0f
        while (x < size.width) {
            drawLine(
                color = LightSecondaryContainer,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
            x += spacing
        }

        var y = 0f
        while (y < size.height) {
            drawLine(
                color = LightSecondaryContainer,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += spacing
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SplashPreview() {
    InventorySalesManagementTheme() {
        SplashScreen(navController = rememberNavController())
    }
}
