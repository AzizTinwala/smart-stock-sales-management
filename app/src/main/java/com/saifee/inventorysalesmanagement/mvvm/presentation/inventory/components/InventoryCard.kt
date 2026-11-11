package com.saifee.inventorysalesmanagement.mvvm.presentation.inventory.components

import androidx.compose.animation.animateContentSize
import com.saifee.inventorysalesmanagement.mvvm.domain.model.InventoryItem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saifee.inventorysalesmanagement.core.ui.system.AppTokens
import com.saifee.inventorysalesmanagement.core.ui.components.AppCard
import com.saifee.inventorysalesmanagement.core.ui.components.AppText

@Composable
fun InventoryCard(
    item: InventoryItem,
    onAdjustClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(AppTokens.CornerRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTokens.Elevation.Low)
    ) {

        Column {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(AppTokens.CornerRadius.Medium))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {

                    if (item.imagePath != null || item.imageUrl != null || item.image != null) {

                        AsyncImage(
                            model = item.imagePath ?: item.imageUrl ?: item.image,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Icon(
                            Icons.Outlined.Inventory2,
                            null,
                            modifier = Modifier.size(AppTokens.IconSize.Large)
                        )

                    }

                }

                Spacer(modifier = Modifier.width(AppTokens.Spacing.Medium))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {

                        AppText(
                            text = item.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(AppTokens.Spacing.Small))

                        AppText(
                            text = "${item.quantity}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (item.lowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }

                    AppText(
                        text = "SKU: ${item.sku}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

                    StockBadge(
                        priority = item.priority,
                        lowStock = item.lowStock
                    )

                    Spacer(modifier = Modifier.height(AppTokens.Spacing.Medium))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        TextButton(
                            onClick = onAdjustClick,
                            contentPadding = PaddingValues(horizontal = AppTokens.Spacing.Small)
                        ) {
                            Icon(
                                Icons.Outlined.Inventory,
                                null,
                                modifier = Modifier.size(AppTokens.IconSize.Compact)
                            )
                            Spacer(modifier = Modifier.width(AppTokens.Spacing.ExtraSmall))
                            AppText("ADJUST", style = MaterialTheme.typography.labelLarge)
                        }

                        Spacer(modifier = Modifier.width(AppTokens.Spacing.Small))

                        Button(
                            onClick = onEditClick,
                            shape = RoundedCornerShape(AppTokens.CornerRadius.Small),
                            contentPadding = PaddingValues(horizontal = AppTokens.Spacing.Medium)
                        ) {
                            Icon(
                                Icons.Outlined.Edit,
                                null,
                                modifier = Modifier.size(AppTokens.IconSize.Compact)
                            )
                            Spacer(modifier = Modifier.width(AppTokens.Spacing.ExtraSmall))
                            AppText("EDIT", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            LinearProgressIndicator(
                progress = { (item.quantity / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = if (item.lowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            )
        }
    }
}
