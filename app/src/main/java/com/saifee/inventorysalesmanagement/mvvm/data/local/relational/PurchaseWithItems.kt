package com.saifee.inventorysalesmanagement.mvvm.data.local.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseItemEntity

data class PurchaseWithItems(
    @Embedded val purchase: PurchaseEntity,
    @Relation(
        parentColumn = "purchaseId",
        entityColumn = "purchaseId"
    )
    val items: List<PurchaseItemEntity>
)
