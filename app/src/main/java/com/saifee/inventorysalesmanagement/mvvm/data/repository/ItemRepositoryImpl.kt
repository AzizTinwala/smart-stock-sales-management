package com.saifee.inventorysalesmanagement.mvvm.data.repository

import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.InventoryLogDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ItemGroupDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.InventoryLogEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ActivityRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val tenantRepository: TenantRepository,
    private val activityRepository: ActivityRepository,
    private val itemDao: ItemDao,
    private val itemGroupDao: ItemGroupDao,
    private val inventoryLogDao: InventoryLogDao,
    private val syncRepository: SyncRepository
) : ItemRepository {

    private val tenantIdsFlow = combine(
        tenantRepository.selectedCompanyIdFlow,
        tenantRepository.selectedLocationIdFlow
    ) { cid, lid -> cid to lid }

    private suspend fun triggerSyncAndBackup() {
        try {
            syncRepository.syncAll()
        } catch (e: Exception) {
            e.printStackTrace()
            syncRepository.triggerFullSync()
        }
    }

    override suspend fun addItemGroup(group: ItemGroupEntity): Long {
        val updatedGroup = group.copy(
            companyId = tenantRepository.getSelectedCompanyId(),
            locationId = tenantRepository.getSelectedLocationId(),
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            createdBy = if (group.createdBy.isEmpty()) tenantRepository.getSelectedUserId() else group.createdBy,
            updatedBy = tenantRepository.getSelectedUserId()
        )
        val id = itemGroupDao.insert(updatedGroup)
        triggerSyncAndBackup()
        return id
    }

    override suspend fun deleteItemGroup(group: ItemGroupEntity) {
        val updatedGroup = group.copy(
            isDeleted = true,
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            updatedBy = tenantRepository.getSelectedUserId()
        )
        itemGroupDao.insert(updatedGroup)
        triggerSyncAndBackup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getItemGroups(): Flow<List<ItemGroupEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        itemGroupDao.getAll(cid ?: "", lid ?: "")
    }

    override suspend fun getItemGroupById(groupId: Long): ItemGroupEntity? {
        return itemGroupDao.getById(
            groupId,
            tenantRepository.getSelectedCompanyId(),
            tenantRepository.getSelectedLocationId()
        )
    }

    override suspend fun addItem(item: ItemEntity): Long {
        val updatedItem = item.copy(
            companyId = tenantRepository.getSelectedCompanyId(),
            locationId = tenantRepository.getSelectedLocationId(),
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            createdBy = if (item.createdBy.isEmpty()) tenantRepository.getSelectedUserId() else item.createdBy,
            updatedBy = tenantRepository.getSelectedUserId()
        )
        val itemId = itemDao.insert(updatedItem)
        activityRepository.addLog(
            type = "ITEM_ADDED",
            message = "Added item: ${updatedItem.itemName}",
            entityId = itemId.toString(),
            entityType = "ITEM"
        )
        triggerSyncAndBackup()
        return itemId
    }

    override suspend fun deleteItem(item: ItemEntity) {
        val updatedItem = item.copy(
            isDeleted = true,
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            updatedBy = tenantRepository.getSelectedUserId()
        )
        itemDao.insert(updatedItem)
        activityRepository.addLog(
            type = "ITEM_DELETED",
            message = "Deleted item: ${updatedItem.itemName}",
            entityId = updatedItem.itemId.toString(),
            entityType = "ITEM"
        )
        triggerSyncAndBackup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getItems(): Flow<List<ItemEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        itemDao.getAll(cid ?: "", lid ?: "")
    }

    override fun getItemsByCompany(companyId: String): Flow<List<ItemEntity>> {
        return itemDao.getAllByCompany(companyId)
    }

    override suspend fun getItemById(itemId: Long): ItemEntity? {
        return itemDao.getById(
            itemId,
            tenantRepository.getSelectedCompanyId(),
            tenantRepository.getSelectedLocationId()
        )
    }

    override suspend fun addInventoryLog(log: InventoryLogEntity) {
        val updatedLog = log.copy(
            companyId = tenantRepository.getSelectedCompanyId(),
            locationId = tenantRepository.getSelectedLocationId(),
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            createdBy = if (log.createdBy.isEmpty()) tenantRepository.getSelectedUserId() else log.createdBy,
            updatedBy = tenantRepository.getSelectedUserId()
        )
        inventoryLogDao.insert(updatedLog)
    }

    override suspend fun adjustStock(itemId: Long, quantity: Double, reason: String, remark: String) {
        val now = System.currentTimeMillis()
        val userId = tenantRepository.getSelectedUserId()
        val companyId = tenantRepository.getSelectedCompanyId()
        val locationId = tenantRepository.getSelectedLocationId()

        itemDao.updateStock(itemId, quantity, now, userId, companyId, locationId)
        
        val fullReason = if (remark.isNotBlank()) "$reason - $remark" else reason

        inventoryLogDao.insert(
            InventoryLogEntity(
                inventoryId = 0L,
                companyId = companyId,
                locationId = locationId,
                itemId = itemId,
                changeType = if (quantity >= 0) "IN" else "OUT",
                quantity = if (quantity >= 0) quantity else -quantity,
                reason = fullReason,
                referenceId = "",
                createdAt = now,
                updatedAt = now,
                createdBy = userId,
                updatedBy = userId,
                isSynced = false
            )
        )

        val itemName = itemDao.getById(itemId, companyId, locationId)?.itemName ?: "Item"
        activityRepository.addLog(
            type = "INVENTORY_ADJUSTED",
            message = "Adjusted stock for $itemName: ${if (quantity >= 0) "+" else ""}$quantity ($fullReason)",
            entityId = itemId.toString(),
            entityType = "ITEM"
        )
        triggerSyncAndBackup()
    }

    override suspend fun adjustStockDelta(itemId: Long, quantityDelta: Double, reason: String, remark: String) {
        val companyId = tenantRepository.getSelectedCompanyId()
        val locationId = tenantRepository.getSelectedLocationId()
        val item = itemDao.getById(itemId, companyId, locationId)
        val currentStock = item?.stockQty ?: 0.0
        val newStock = currentStock + quantityDelta

        // If stock goes negative, adjust accordingly or allow with audit log
        adjustStock(itemId, quantityDelta, reason, if (newStock < 0.0) "$remark [WARNING: Negative Stock Resulting: $newStock]" else remark)
    }
}

