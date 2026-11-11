package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable

@Composable
fun AppAnimatedVisibility(
    visible: Boolean,
    delay: Int = 0,
    duration: Int = 500,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = duration,
                delayMillis = delay
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = duration,
                delayMillis = delay
            )
        ) { it / 2 }
    ) {
        content()
    }
}
