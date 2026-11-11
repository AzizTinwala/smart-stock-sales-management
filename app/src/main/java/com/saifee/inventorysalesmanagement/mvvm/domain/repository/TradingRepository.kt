package com.saifee.inventorysalesmanagement.mvvm.domain.repository

import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentReceivedEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentsOutEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnItemEntity
import com.saifee.inventorysalesmanagement.mvvm.domain.model.InvoiceDetail
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.relational.PurchaseWithItems
import com.saifee.inventorysalesmanagement.mvvm.data.local.relational.SalesWithItems
import kotlinx.coroutines.flow.Flow

interface TradingRepository {
    // CUSTOMER
    suspend fun addCustomer(customer: CustomerEntity)
    suspend fun deleteCustomer(customer: CustomerEntity)
    fun getCustomers(): Flow<List<CustomerEntity>>
    suspend fun getCustomerById(customerId: String): CustomerEntity?

    // VENDOR
    suspend fun addVendor(vendor: VendorEntity)
    suspend fun deleteVendor(vendor: VendorEntity)
    fun getVendors(): Flow<List<VendorEntity>>
    suspend fun getVendorById(vendorId: String): VendorEntity?

    // PURCHASE
    suspend fun addPurchase(purchase: PurchaseEntity, items: List<PurchaseItemEntity>)
    suspend fun updatePurchase(purchase: PurchaseEntity, items: List<PurchaseItemEntity>)
    suspend fun deletePurchase(purchaseId: String)
    fun getPurchases(): Flow<List<PurchaseEntity>>
    suspend fun getPurchaseById(purchaseId: String): PurchaseEntity?
    suspend fun getPurchaseWithItems(purchaseId: String): PurchaseWithItems?

    // SALES
    suspend fun addSales(sales: SalesEntity, items: List<SalesItemEntity>)
    suspend fun updateSales(sales: SalesEntity, items: List<SalesItemEntity>)
    suspend fun deleteSales(salesId: String)
    fun getSales(): Flow<List<SalesEntity>>
    fun getSalesByCompany(companyId: String): Flow<List<SalesEntity>>
    fun getSalesItemsByCompany(companyId: String): Flow<List<SalesItemEntity>>
    suspend fun getSalesById(salesId: String): SalesEntity?
    suspend fun getSalesWithItems(salesId: String): SalesWithItems?
    suspend fun getInvoiceDetail(salesId: String): InvoiceDetail?

    // SALES RETURN
    suspend fun addSalesReturn(returnData: SalesReturnEntity, items: List<SalesReturnItemEntity>)

    // PAYMENTS
    suspend fun addPaymentReceived(payment: PaymentReceivedEntity)
    suspend fun addPaymentOut(payment: PaymentsOutEntity)
    fun getPaymentsReceived(): Flow<List<PaymentReceivedEntity>>
    fun getPaymentsOut(): Flow<List<PaymentsOutEntity>>
}
