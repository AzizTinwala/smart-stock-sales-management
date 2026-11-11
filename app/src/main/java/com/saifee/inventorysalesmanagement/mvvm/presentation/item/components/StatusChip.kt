package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun StatusChip(

    value: Int,

    label: String,

    color: Color

) {

    Text(

        text = buildAnnotatedString {

            withStyle(
                SpanStyle(
                    color = color
                )
            ) {

                append(value.toString())

            }

            append(" ")

            append(label)

        },

        style = MaterialTheme.typography.labelLarge

    )

}