package com.saifee.barcode_scanner

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun ScanningAnimation(
    lineColor: Color = MaterialTheme.colorScheme.primary,
    frameColor: Color = MaterialTheme.colorScheme.onSurface
) {

    val infiniteTransition = rememberInfiniteTransition(
        label = "Paytm QR Animation"
    )

    /*
     * Scan line continuously moves
     * from top to bottom.
     */
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "ScanLine"
    )

    /*
     * Corner pulse animation
     */
    val cornerPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CornerPulse"
    )

    /*
     * Laser glow pulse
     */
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Glow"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val screenWidth = size.width
            val screenHeight = size.height

            /*
             * QR scanner frame
             */
            val frameSize = 260.dp.toPx()

            val left =
                (screenWidth - frameSize) / 2f

            val top =
                (screenHeight - frameSize) / 2f

            val cornerRadius =
                20.dp.toPx()

            /*
             * -----------------------------------
             * 1. DARK OVERLAY
             * -----------------------------------
             */

            drawRect(
                color = Color.Black.copy(
                    alpha = 0.65f
                ),
                size = size
            )

            /*
             * -----------------------------------
             * 2. CLEAR QR AREA
             * -----------------------------------
             */

            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(
                    frameSize,
                    frameSize
                ),
                cornerRadius = CornerRadius(
                    cornerRadius,
                    cornerRadius
                ),
                blendMode = BlendMode.Clear
            )

            /*
             * -----------------------------------
             * 3. SUBTLE QR BORDER
             * -----------------------------------
             */

            drawRoundRect(
                color = Color.White.copy(
                    alpha = 0.18f
                ),
                topLeft = Offset(left, top),
                size = Size(
                    frameSize,
                    frameSize
                ),
                cornerRadius = CornerRadius(
                    cornerRadius,
                    cornerRadius
                ),
                style = Stroke(
                    width = 1.dp.toPx()
                )
            )

            /*
             * -----------------------------------
             * 4. SCAN LINE POSITION
             * -----------------------------------
             */

            val lineY =
                top + (frameSize * scanProgress)

            /*
             * -----------------------------------
             * 5. LARGE BLUE GLOW
             * -----------------------------------
             */

            val glowHeight =
                40.dp.toPx()

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        lineColor.copy(
                            alpha = 0.10f * glowAlpha
                        ),
                        lineColor.copy(
                            alpha = 0.45f * glowAlpha
                        ),
                        lineColor.copy(
                            alpha = 0.10f * glowAlpha
                        ),
                        Color.Transparent
                    ),
                    startY = lineY - glowHeight,
                    endY = lineY + glowHeight
                ),
                topLeft = Offset(
                    left,
                    lineY - glowHeight
                ),
                size = Size(
                    frameSize,
                    glowHeight * 2
                )
            )

            /*
             * -----------------------------------
             * 6. BRIGHT CENTER LASER
             * -----------------------------------
             */

            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        lineColor.copy(alpha = 0.8f),
                        Color.White,
                        lineColor.copy(alpha = 0.8f),
                        Color.Transparent
                    ),
                    startX = left,
                    endX = left + frameSize
                ),
                start = Offset(left, lineY),
                end = Offset(
                    left + frameSize,
                    lineY
                ),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            /*
             * -----------------------------------
             * 7. ANIMATED CORNERS
             * -----------------------------------
             */

            val cornerLength =
                42.dp.toPx() * cornerPulse

            val strokeWidth =
                4.dp.toPx()

            val cornerColor =
                frameColor.copy(
                    alpha = cornerPulse
                )

            // TOP LEFT

            drawLine(
                color = cornerColor,
                start = Offset(left, top),
                end = Offset(
                    left + cornerLength,
                    top
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = cornerColor,
                start = Offset(left, top),
                end = Offset(
                    left,
                    top + cornerLength
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )


            // TOP RIGHT

            drawLine(
                color = cornerColor,
                start = Offset(
                    left + frameSize,
                    top
                ),
                end = Offset(
                    left + frameSize - cornerLength,
                    top
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = cornerColor,
                start = Offset(
                    left + frameSize,
                    top
                ),
                end = Offset(
                    left + frameSize,
                    top + cornerLength
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )


            // BOTTOM LEFT

            drawLine(
                color = cornerColor,
                start = Offset(
                    left,
                    top + frameSize
                ),
                end = Offset(
                    left + cornerLength,
                    top + frameSize
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = cornerColor,
                start = Offset(
                    left,
                    top + frameSize
                ),
                end = Offset(
                    left,
                    top + frameSize - cornerLength
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )


            // BOTTOM RIGHT

            drawLine(
                color = cornerColor,
                start = Offset(
                    left + frameSize,
                    top + frameSize
                ),
                end = Offset(
                    left + frameSize - cornerLength,
                    top + frameSize
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = cornerColor,
                start = Offset(
                    left + frameSize,
                    top + frameSize
                ),
                end = Offset(
                    left + frameSize,
                    top + frameSize - cornerLength
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}