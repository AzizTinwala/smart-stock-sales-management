package com.saifee.inventorysalesmanagement.mvvm.data.local.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity

data class ItemWithGroup(
    @Embedded val item: ItemEntity,

    @Relation(
        parentColumn = "groupId",
        entityColumn = "groupId"
    )
    val group: ItemGroupEntity
)