package com.saifee.barcode_scanner


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun AnimatedQrCode(
    qrPainter: Painter,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {

    val infiniteTransition = rememberInfiniteTransition(
        label = "Paytm QR Animation"
    )

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "QR Scan"
    )

    val glowProgress by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "QR Glow"
    )
    
    val centerLineColor = MaterialTheme.colorScheme.onPrimary

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .clip(
                androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
    ) {

        /*
         * Draw QR Code
         */
        with(qrPainter) {
            draw(
                size = size
            )
        }

        clipRect {

            /*
             * Scan line position
             */
            val lineY = size.height * progress

            val glowHeight = 35.dp.toPx()

            /*
             * Blue glowing area
             */
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        lineColor.copy(
                            alpha = 0.08f * glowProgress
                        ),
                        lineColor.copy(
                            alpha = 0.35f * glowProgress
                        ),
                        lineColor.copy(
                            alpha = 0.08f * glowProgress
                        ),
                        Color.Transparent
                    ),
                    startY = lineY - glowHeight,
                    endY = lineY + glowHeight
                ),
                topLeft = androidx.compose.ui.geometry.Offset(
                    0f,
                    lineY - glowHeight
                ),
                size = androidx.compose.ui.geometry.Size(
                    size.width,
                    glowHeight * 2
                )
            )

            /*
             * Bright scan line
             */
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        lineColor,
                        centerLineColor,
                        lineColor,
                        Color.Transparent
                    )
                ),
                start = androidx.compose.ui.geometry.Offset(
                    0f,
                    lineY
                ),
                end = androidx.compose.ui.geometry.Offset(
                    size.width,
                    lineY
                ),
                strokeWidth = 3.dp.toPx()
            )
        }
    }
}