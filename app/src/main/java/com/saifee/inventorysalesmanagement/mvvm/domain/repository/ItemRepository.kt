package com.saifee.inventorysalesmanagement.mvvm.domain.repository

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.InventoryLogEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    // ITEM GROUP
    suspend fun addItemGroup(group: ItemGroupEntity): Long
    suspend fun deleteItemGroup(group: ItemGroupEntity)
    fun getItemGroups(): Flow<List<ItemGroupEntity>>
    suspend fun getItemGroupById(groupId: Long): ItemGroupEntity?

    // ITEM
    suspend fun addItem(item: ItemEntity): Long
    suspend fun deleteItem(item: ItemEntity)
    fun getItems(): Flow<List<ItemEntity>>
    fun getItemsByCompany(companyId: String): Flow<List<ItemEntity>>
    suspend fun getItemById(itemId: Long): ItemEntity?

    // INVENTORY
    suspend fun addInventoryLog(log: InventoryLogEntity)
    suspend fun adjustStock(itemId: Long, quantity: Double, reason: String, remark: String = "")
    suspend fun adjustStockDelta(itemId: Long, quantityDelta: Double, reason: String, remark: String = "")
}
