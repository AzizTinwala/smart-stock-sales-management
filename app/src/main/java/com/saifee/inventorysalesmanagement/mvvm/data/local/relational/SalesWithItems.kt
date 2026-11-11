package com.saifee.inventorysalesmanagement.mvvm.data.local.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity

data class SalesWithItems(

    @Embedded val sales: SalesEntity,

    @Relation(
        parentColumn = "salesId",
        entityColumn = "salesId"
    )
    val items: List<SalesItemEntity>
)