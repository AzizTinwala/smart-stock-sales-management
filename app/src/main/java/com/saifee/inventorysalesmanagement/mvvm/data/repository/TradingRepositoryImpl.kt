package com.saifee.inventorysalesmanagement.mvvm.data.repository

import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.CustomerDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.InventoryLogDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PaymentReceivedDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PaymentsOutDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PurchaseDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PurchaseItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesReturnDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesReturnItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.VendorDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.InventoryLogEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentReceivedEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentsOutEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.relational.PurchaseWithItems
import com.saifee.inventorysalesmanagement.mvvm.data.local.relational.SalesWithItems
import com.saifee.inventorysalesmanagement.mvvm.domain.model.InvoiceDetail
import com.saifee.inventorysalesmanagement.mvvm.domain.model.InvoiceItem
import androidx.room.withTransaction
import com.saifee.inventorysalesmanagement.mvvm.data.local.DataBase
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ActivityRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class TradingRepositoryImpl @Inject constructor(
    private val db: DataBase,
    private val tenantRepository: TenantRepository,
    private val activityRepository: ActivityRepository,
    private val customerDao: CustomerDao,
    private val vendorDao: VendorDao,
    private val purchaseDao: PurchaseDao,
    private val purchaseItemDao: PurchaseItemDao,
    private val salesDao: SalesDao,
    private val salesItemDao: SalesItemDao,
    private val salesReturnDao: SalesReturnDao,
    private val salesReturnItemDao: SalesReturnItemDao,
    private val paymentReceivedDao: PaymentReceivedDao,
    private val paymentsOutDao: PaymentsOutDao,
    private val itemDao: ItemDao,
    private val inventoryLogDao: InventoryLogDao,
    private val syncRepository: SyncRepository
) : TradingRepository {

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

    override suspend fun addCustomer(customer: CustomerEntity) {
        val updatedCustomer = customer.copy(
            companyId = tenantRepository.getSelectedCompanyId(),
            locationId = tenantRepository.getSelectedLocationId(),
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            createdBy = if (customer.createdBy.isEmpty()) tenantRepository.getSelectedUserId() else customer.createdBy,
            updatedBy = tenantRepository.getSelectedUserId()
        )
        customerDao.insert(updatedCustomer)
        activityRepository.addLog(
            type = "CUSTOMER_ADDED",
            message = "Added customer: ${updatedCustomer.name}",
            entityId = updatedCustomer.customerId,
            entityType = "CUSTOMER"
        )
        triggerSyncAndBackup()
    }

    override suspend fun deleteCustomer(customer: CustomerEntity) {
        val updatedCustomer = customer.copy(
            isDeleted = true,
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            updatedBy = tenantRepository.getSelectedUserId()
        )
        customerDao.insert(updatedCustomer)
        activityRepository.addLog(
            type = "CUSTOMER_DELETED",
            message = "Deleted customer: ${updatedCustomer.name}",
            entityId = updatedCustomer.customerId,
            entityType = "CUSTOMER"
        )
        triggerSyncAndBackup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCustomers(): Flow<List<CustomerEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        customerDao.getAll(cid ?: "", lid ?: "")
    }

    override suspend fun getCustomerById(customerId: String): CustomerEntity? {
        return customerDao.getById(
            customerId,
            tenantRepository.getSelectedCompanyId(),
            tenantRepository.getSelectedLocationId()
        )
    }

    override suspend fun addVendor(vendor: VendorEntity) {
        val updatedVendor = vendor.copy(
            companyId = tenantRepository.getSelectedCompanyId(),
            locationId = tenantRepository.getSelectedLocationId(),
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            createdBy = if (vendor.createdBy.isEmpty()) tenantRepository.getSelectedUserId() else vendor.createdBy,
            updatedBy = tenantRepository.getSelectedUserId()
        )
        vendorDao.insert(updatedVendor)
        activityRepository.addLog(
            type = "VENDOR_ADDED",
            message = "Added vendor: ${updatedVendor.name}",
            entityId = updatedVendor.vendorId,
            entityType = "VENDOR"
        )
        triggerSyncAndBackup()
    }

    override suspend fun deleteVendor(vendor: VendorEntity) {
        val updatedVendor = vendor.copy(
            isDeleted = true,
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            updatedBy = tenantRepository.getSelectedUserId()
        )
        vendorDao.insert(updatedVendor)
        activityRepository.addLog(
            type = "VENDOR_DELETED",
            message = "Deleted vendor: ${updatedVendor.name}",
            entityId = updatedVendor.vendorId,
            entityType = "VENDOR"
        )
        triggerSyncAndBackup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getVendors(): Flow<List<VendorEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        vendorDao.getAll(cid ?: "", lid ?: "")
    }

    override suspend fun getVendorById(vendorId: String): VendorEntity? {
        return vendorDao.getById(
            vendorId,
            tenantRepository.getSelectedCompanyId(),
            tenantRepository.getSelectedLocationId()
        )
    }

    override suspend fun addPurchase(purchase: PurchaseEntity, items: List<PurchaseItemEntity>) {
        db.withTransaction {
            val now = System.currentTimeMillis()
            val companyId = tenantRepository.getSelectedCompanyId()
            val locationId = tenantRepository.getSelectedLocationId()
            val userId = tenantRepository.getSelectedUserId()

            val updatedPurchase = purchase.copy(
                companyId = companyId,
                locationId = locationId,
                isSynced = false,
                updatedAt = now,
                createdBy = if (purchase.createdBy.isEmpty()) userId else purchase.createdBy,
                updatedBy = userId
            )
            purchaseDao.insert(updatedPurchase)

            val updatedItems = items.map {
                it.copy(
                    companyId = companyId,
                    locationId = locationId,
                    isSynced = false,
                    updatedAt = now,
                    createdBy = if (it.createdBy.isEmpty()) userId else it.createdBy,
                    updatedBy = userId
                )
            }
            purchaseItemDao.insertAll(updatedItems)

            updatedItems.forEach { purchaseItem ->
                itemDao.updateStock(purchaseItem.itemId, purchaseItem.quantity, now, userId, companyId, locationId)
                inventoryLogDao.insert(
                    InventoryLogEntity(
                        inventoryId = 0L,
                        companyId = companyId,
                        locationId = locationId,
                        itemId = purchaseItem.itemId,
                        changeType = "IN",
                        quantity = purchaseItem.quantity,
                        reason = "Purchase: ${updatedPurchase.invoiceNumber}",
                        referenceId = updatedPurchase.purchaseId,
                        createdAt = now,
                        updatedAt = now,
                        createdBy = userId,
                        updatedBy = userId,
                        isSynced = false
                    )
                )
            }

            activityRepository.addLog(
                type = "PURCHASE_CREATED",
                message = "Purchase order: ${updatedPurchase.invoiceNumber}",
                amount = updatedPurchase.totalAmount,
                transactionType = "EXPENSE",
                entityId = updatedPurchase.purchaseId,
                entityType = "PURCHASE"
            )
        }
        triggerSyncAndBackup()
    }

    override suspend fun updatePurchase(purchase: PurchaseEntity, items: List<PurchaseItemEntity>) {
        db.withTransaction {
            val now = System.currentTimeMillis()
            val companyId = tenantRepository.getSelectedCompanyId()
            val locationId = tenantRepository.getSelectedLocationId()
            val userId = tenantRepository.getSelectedUserId()

            val oldItems = purchaseItemDao.getByPurchaseId(purchase.purchaseId, companyId, locationId)

            oldItems.forEach { item ->
                itemDao.updateStock(item.itemId, -item.quantity, now, userId, companyId, locationId)
            }

            purchaseItemDao.deleteByPurchaseId(purchase.purchaseId, companyId, locationId)

            purchaseDao.insert(purchase.copy(isSynced = false, updatedAt = now, updatedBy = userId))

            val updatedItems = items.map {
                it.copy(
                    companyId = companyId,
                    locationId = locationId,
                    isSynced = false,
                    updatedAt = now,
                    updatedBy = userId
                )
            }
            purchaseItemDao.insertAll(updatedItems)

            updatedItems.forEach { purchaseItem ->
                itemDao.updateStock(purchaseItem.itemId, purchaseItem.quantity, now, userId, companyId, locationId)
                inventoryLogDao.insert(
                    InventoryLogEntity(
                        inventoryId = 0L,
                        companyId = companyId,
                        locationId = locationId,
                        itemId = purchaseItem.itemId,
                        changeType = "IN",
                        quantity = purchaseItem.quantity,
                        reason = "Purchase Update: ${purchase.invoiceNumber}",
                        referenceId = purchase.purchaseId,
                        createdAt = now,
                        updatedAt = now,
                        createdBy = userId,
                        updatedBy = userId,
                        isSynced = false
                    )
                )
            }

            activityRepository.addLog(
                type = "PURCHASE_UPDATED",
                message = "Purchase updated: ${purchase.invoiceNumber}",
                amount = purchase.totalAmount,
                transactionType = "EXPENSE",
                entityId = purchase.purchaseId,
                entityType = "PURCHASE"
            )
        }
        triggerSyncAndBackup()
    }

    override suspend fun deletePurchase(purchaseId: String) {
        db.withTransaction {
            val companyId = tenantRepository.getSelectedCompanyId()
            val locationId = tenantRepository.getSelectedLocationId()
            val userId = tenantRepository.getSelectedUserId()
            
            val purchase = purchaseDao.getById(purchaseId, companyId, locationId) ?: return@withTransaction
            val items = purchaseItemDao.getByPurchaseId(purchaseId, companyId, locationId)
            val now = System.currentTimeMillis()

            items.forEach { purchaseItem ->
                itemDao.updateStock(purchaseItem.itemId, -purchaseItem.quantity, now, userId, companyId, locationId)
                inventoryLogDao.insert(
                    InventoryLogEntity(
                        inventoryId = 0L,
                        companyId = companyId,
                        locationId = locationId,
                        itemId = purchaseItem.itemId,
                        changeType = "OUT",
                        quantity = purchaseItem.quantity,
                        reason = "Purchase Delete: ${purchase.invoiceNumber}",
                        referenceId = purchase.purchaseId,
                        createdAt = now,
                        updatedAt = now,
                        createdBy = userId,
                        updatedBy = userId,
                        isSynced = false
                    )
                )
            }

            val updatedPurchase = purchase.copy(isDeleted = true, isSynced = false, updatedAt = now, updatedBy = userId)
            purchaseDao.insert(updatedPurchase)
            activityRepository.addLog(
                type = "PURCHASE_DELETED",
                message = "Purchase deleted: ${updatedPurchase.invoiceNumber}",
                entityId = updatedPurchase.purchaseId,
                entityType = "PURCHASE"
            )
        }
        triggerSyncAndBackup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPurchases(): Flow<List<PurchaseEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        purchaseDao.getAll(cid ?: "", lid ?: "")
    }

    override suspend fun getPurchaseById(purchaseId: String): PurchaseEntity? {
        return purchaseDao.getById(
            purchaseId,
            tenantRepository.getSelectedCompanyId(),
            tenantRepository.getSelectedLocationId()
        )
    }

    override suspend fun getPurchaseWithItems(purchaseId: String): PurchaseWithItems? {
        return purchaseDao.getPurchaseWithItems(
            purchaseId,
            tenantRepository.getSelectedCompanyId(),
            tenantRepository.getSelectedLocationId()
        )
    }

    override suspend fun addSales(sales: SalesEntity, items: List<SalesItemEntity>) {
        db.withTransaction {
            val now = System.currentTimeMillis()
            val companyId = tenantRepository.getSelectedCompanyId()
            val locationId = tenantRepository.getSelectedLocationId()
            val userId = tenantRepository.getSelectedUserId()

            val updatedSales = sales.copy(
                companyId = companyId,
                locationId = locationId,
                isSynced = false,
                updatedAt = now,
                createdBy = if (sales.createdBy.isEmpty()) userId else sales.createdBy,
                updatedBy = userId
            )
            salesDao.insert(updatedSales)

            val updatedItems = items.map {
                it.copy(
                    companyId = companyId,
                    locationId = locationId,
                    isSynced = false,
                    updatedAt = now,
                    createdBy = if (it.createdBy.isEmpty()) userId else it.createdBy,
                    updatedBy = userId
                )
            }
            salesItemDao.insertAll(updatedItems)

            updatedItems.forEach { saleItem ->
                itemDao.updateStock(saleItem.itemId, -saleItem.quantity, now, userId, companyId, locationId)
                inventoryLogDao.insert(
                    InventoryLogEntity(
                        inventoryId = 0L,
                        companyId = companyId,
                        locationId = locationId,
                        itemId = saleItem.itemId,
                        changeType = "OUT",
                        quantity = saleItem.quantity,
                        reason = "Sale: ${updatedSales.invoiceNumber}",
                        referenceId = updatedSales.salesId,
                        createdAt = now,
                        updatedAt = now,
                        createdBy = userId,
                        updatedBy = userId,
                        isSynced = false
                    )
                )
            }

            activityRepository.addLog(
                type = "SALE_CREATED",
                message = "Sale invoice: ${updatedSales.invoiceNumber}",
                amount = updatedSales.finalAmount,
                transactionType = "INCOME",
                entityId = updatedSales.salesId,
                entityType = "SALE"
            )
        }
        triggerSyncAndBackup()
    }

    override suspend fun updateSales(sales: SalesEntity, items: List<SalesItemEntity>) {
        db.withTransaction {
            val now = System.currentTimeMillis()
            val companyId = tenantRepository.getSelectedCompanyId()
            val locationId = tenantRepository.getSelectedLocationId()
            val userId = tenantRepository.getSelectedUserId()

            val oldItems = salesItemDao.getBySalesId(sales.salesId, companyId, locationId)

            oldItems.forEach { item ->
                itemDao.updateStock(item.itemId, item.quantity, now, userId, companyId, locationId)
            }

            salesItemDao.deleteBySalesId(sales.salesId, companyId, locationId)

            salesDao.insert(sales.copy(isSynced = false, updatedAt = now, updatedBy = userId))

            val updatedItems = items.map {
                it.copy(
                    companyId = companyId,
                    locationId = locationId,
                    isSynced = false,
                    updatedAt = now,
                    updatedBy = userId
                )
            }
            salesItemDao.insertAll(updatedItems)

            updatedItems.forEach { saleItem ->
                itemDao.updateStock(saleItem.itemId, -saleItem.quantity, now, userId, companyId, locationId)
                inventoryLogDao.insert(
                    InventoryLogEntity(
                        inventoryId = 0L,
                        companyId = companyId,
                        locationId = locationId,
                        itemId = saleItem.itemId,
                        changeType = "OUT",
                        quantity = saleItem.quantity,
                        reason = "Sale Update: ${sales.invoiceNumber}",
                        referenceId = sales.salesId,
                        createdAt = now,
                        updatedAt = now,
                        createdBy = userId,
                        updatedBy = userId,
                        isSynced = false
                    )
                )
            }

            activityRepository.addLog(
                type = "SALE_UPDATED",
                message = "Sale updated: ${sales.invoiceNumber}",
                amount = sales.finalAmount,
                transactionType = "INCOME",
                entityId = sales.salesId,
                entityType = "SALE"
            )
        }
        triggerSyncAndBackup()
    }

    override suspend fun deleteSales(salesId: String) {
        db.withTransaction {
            val companyId = tenantRepository.getSelectedCompanyId()
            val locationId = tenantRepository.getSelectedLocationId()
            val userId = tenantRepository.getSelectedUserId()

            val sale = salesDao.getById(salesId, companyId, locationId) ?: return@withTransaction
            val items = salesItemDao.getBySalesId(salesId, companyId, locationId)
            val now = System.currentTimeMillis()

            items.forEach { saleItem ->
                itemDao.updateStock(saleItem.itemId, saleItem.quantity, now, userId, companyId, locationId)
                inventoryLogDao.insert(
                    InventoryLogEntity(
                        inventoryId = 0L,
                        companyId = companyId,
                        locationId = locationId,
                        itemId = saleItem.itemId,
                        changeType = "IN",
                        quantity = saleItem.quantity,
                        reason = "Sale Delete: ${sale.invoiceNumber}",
                        referenceId = sale.salesId,
                        createdAt = now,
                        updatedAt = now,
                        createdBy = userId,
                        updatedBy = userId,
                        isSynced = false
                    )
                )
            }

            val updatedSale = sale.copy(isDeleted = true, isSynced = false, updatedAt = now, updatedBy = userId)
            salesDao.insert(updatedSale)
            activityRepository.addLog(
                type = "SALE_DELETED",
                message = "Sale deleted: ${updatedSale.invoiceNumber}",
                entityId = updatedSale.salesId,
                entityType = "SALE"
            )
        }
        triggerSyncAndBackup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSales(): Flow<List<SalesEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        salesDao.getAll(cid ?: "", lid ?: "")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSalesByCompany(companyId: String): Flow<List<SalesEntity>> = salesDao.getAllByCompany(companyId)

    override fun getSalesItemsByCompany(companyId: String): Flow<List<SalesItemEntity>> {
        return salesItemDao.getAllByCompany(companyId)
    }

    override suspend fun getSalesById(salesId: String): SalesEntity? {
        return salesDao.getById(
            salesId,
            tenantRepository.getSelectedCompanyId(),
            tenantRepository.getSelectedLocationId()
        )
    }

    override suspend fun getSalesWithItems(salesId: String): SalesWithItems? {
        return salesDao.getSalesWithItems(
            salesId,
            tenantRepository.getSelectedCompanyId(),
            tenantRepository.getSelectedLocationId()
        )
    }

    override suspend fun getInvoiceDetail(salesId: String): InvoiceDetail? {
        val companyId = tenantRepository.getSelectedCompanyId()
        val locationId = tenantRepository.getSelectedLocationId()
        
        val salesWithItems = salesDao.getSalesWithItems(salesId, companyId, locationId) ?: return null
        val customer = if (salesWithItems.sales.customerId != "CASH") {
            salesWithItems.sales.customerId?.let { customerDao.getById(it, companyId, locationId) }
        } else null
        
        val company = tenantRepository.getCompanyById(companyId) ?: return null
        
        val items = salesWithItems.items.map { saleItem ->
            val itemDetails = itemDao.getById(saleItem.itemId, companyId, locationId)
            InvoiceItem(
                itemName = itemDetails?.itemName ?: "Unknown Item",
                sku = itemDetails?.sku,
                quantity = saleItem.quantity,
                price = saleItem.price,
                total = saleItem.total,
                gstPercentage = itemDetails?.gstPercentage ?: 0.0
            )
        }
        
        return InvoiceDetail(
            sale = salesWithItems.sales,
            items = items,
            customer = customer,
            company = company
        )
    }

    override suspend fun addSalesReturn(returnData: SalesReturnEntity, items: List<SalesReturnItemEntity>) {
        db.withTransaction {
            val now = System.currentTimeMillis()
            val companyId = tenantRepository.getSelectedCompanyId()
            val locationId = tenantRepository.getSelectedLocationId()
            val userId = tenantRepository.getSelectedUserId()

            val updatedReturn = returnData.copy(
                companyId = companyId,
                locationId = locationId,
                isSynced = false,
                updatedAt = now,
                createdBy = if (returnData.createdBy.isEmpty()) userId else returnData.createdBy,
                updatedBy = userId
            )
            salesReturnDao.insert(updatedReturn)

            val updatedItems = items.map {
                it.copy(
                    companyId = companyId,
                    locationId = locationId,
                    isSynced = false,
                    updatedAt = now,
                    createdBy = if (it.createdBy.isEmpty()) userId else it.createdBy,
                    updatedBy = userId
                )
            }
            salesReturnItemDao.insertAll(updatedItems)

            updatedItems.forEach { returnItem ->
                if (returnItem.restockAction == "RESTOCK") {
                    itemDao.updateStock(returnItem.itemId, returnItem.quantity, now, userId, companyId, locationId)
                    inventoryLogDao.insert(
                        InventoryLogEntity(
                            inventoryId = 0L,
                            companyId = companyId,
                            locationId = locationId,
                            itemId = returnItem.itemId,
                            changeType = "IN",
                            quantity = returnItem.quantity,
                            reason = "Sales Return: ${updatedReturn.salesReturnId}",
                            referenceId = updatedReturn.salesReturnId,
                            createdAt = now,
                            updatedAt = now,
                            createdBy = userId,
                            updatedBy = userId,
                            isSynced = false
                        )
                    )
                }
            }

            activityRepository.addLog(
                type = "SALES_RETURN_CREATED",
                message = "Sales return processed: ${updatedReturn.salesReturnId}",
                amount = updatedReturn.totalAmount,
                transactionType = "EXPENSE",
                entityId = updatedReturn.salesReturnId,
                entityType = "SALES_RETURN"
            )
        }
        triggerSyncAndBackup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPaymentsReceived(): Flow<List<PaymentReceivedEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        paymentReceivedDao.getAll(cid ?: "", lid ?: "")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPaymentsOut(): Flow<List<PaymentsOutEntity>> = tenantIdsFlow.flatMapLatest { (cid, lid) ->
        paymentsOutDao.getAll(cid ?: "", lid ?: "")
    }

    override suspend fun addPaymentReceived(payment: PaymentReceivedEntity) {
        val updatedPayment = payment.copy(
            companyId = tenantRepository.getSelectedCompanyId(),
            locationId = tenantRepository.getSelectedLocationId(),
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            createdBy = if (payment.createdBy.isEmpty()) tenantRepository.getSelectedUserId() else payment.createdBy,
            updatedBy = tenantRepository.getSelectedUserId()
        )
        paymentReceivedDao.insert(updatedPayment)
        
        activityRepository.addLog(
            type = "PAYMENT_IN",
            message = "Payment received from ${updatedPayment.customerId}: ₹${updatedPayment.amount}",
            amount = updatedPayment.amount,
            transactionType = "INCOME",
            entityId = updatedPayment.paymentReceivedId,
            entityType = "PAYMENT_RECEIVED"
        )
        triggerSyncAndBackup()
    }

    override suspend fun addPaymentOut(payment: PaymentsOutEntity) {
        val updatedPayment = payment.copy(
            companyId = tenantRepository.getSelectedCompanyId(),
            locationId = tenantRepository.getSelectedLocationId(),
            isSynced = false,
            updatedAt = System.currentTimeMillis(),
            createdBy = if (payment.createdBy.isEmpty()) tenantRepository.getSelectedUserId() else payment.createdBy,
            updatedBy = tenantRepository.getSelectedUserId()
        )
        paymentsOutDao.insert(updatedPayment)

        activityRepository.addLog(
            type = "PAYMENT_OUT",
            message = "Payment to ${updatedPayment.vendorId}: ₹${updatedPayment.amount}",
            amount = updatedPayment.amount,
            transactionType = "EXPENSE",
            entityId = updatedPayment.paymentOutId,
            entityType = "PAYMENT_OUT"
        )
        triggerSyncAndBackup()
    }
}
