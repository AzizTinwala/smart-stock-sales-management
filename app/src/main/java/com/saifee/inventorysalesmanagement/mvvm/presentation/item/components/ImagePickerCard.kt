package com.saifee.inventorysalesmanagement.mvvm.presentation.item.components


import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun ImagePickerCard(
    imageModel: Any?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {

    AnimatedContent(
        targetState = imageModel,
        label = ""
    ) { model ->

        if (model == null) {

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable { onPickImage() },

                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Outlined.AddAPhoto,
                        null,
                        modifier = Modifier.size(52.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppText(
                        "Tap to Upload Image",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    AppText(
                        "Camera or Gallery",
                        style = MaterialTheme.typography.bodyMedium
                    )

                }

            }

        } else {

            Box {

                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )

                FilledIconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    onClick = onRemoveImage
                ) {

                    Icon(
                        Icons.Outlined.Delete,
                        null
                    )

                }

            }

        }

    }

}