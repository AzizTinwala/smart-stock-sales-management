package com.saifee.inventorysalesmanagement.core.adaptive

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.saifee.inventorysalesmanagement.core.enums.NavigationMode

@Composable
fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val adaptiveContext = LocalAdaptiveContext.current
    
    // In an enterprise platform, this component would decide whether to show 
    // a BottomBar or a NavigationRail/PermanentDrawer based on adaptiveContext.navigationMode
    
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = if (adaptiveContext.navigationMode == NavigationMode.BOTTOM_BAR) bottomBar else ({}),
        floatingActionButton = floatingActionButton,
        content = content
    )
}
