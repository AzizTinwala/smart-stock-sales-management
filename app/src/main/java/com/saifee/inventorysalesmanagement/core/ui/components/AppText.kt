package com.saifee.inventorysalesmanagement.core.ui.components

import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.saifee.inventorysalesmanagement.core.enums.AppTextStyle

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: AppTextStyle = AppTextStyle.BODY,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val (textStyle, defaultColor) = when (style) {
        AppTextStyle.DISPLAY -> typography.displaySmall to colorScheme.onSurfaceVariant
        AppTextStyle.HEADLINE -> typography.headlineSmall to colorScheme.onBackground
        AppTextStyle.TITLE -> typography.titleMedium to colorScheme.onBackground
        AppTextStyle.BODY -> typography.bodyLarge to colorScheme.outline
        AppTextStyle.LABEL -> typography.labelMedium to colorScheme.outline
    }

    val finalColor = if (color != Color.Unspecified) color else defaultColor

    AppText(
        text = text,
        modifier = modifier,
        style = textStyle,
        color = finalColor,
        fontWeight = fontWeight,
        maxLines = maxLines
    )
}

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    val defaultStyle = MaterialTheme.typography.bodyLarge
    val mergedStyle = if (style == TextStyle.Default) defaultStyle else style

    Text(
        text = text,
        modifier = modifier,
        style = mergedStyle,
        color = color,
        maxLines = maxLines,
        autoSize = autoSize,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        minLines = minLines,
        onTextLayout = onTextLayout
    )
}

