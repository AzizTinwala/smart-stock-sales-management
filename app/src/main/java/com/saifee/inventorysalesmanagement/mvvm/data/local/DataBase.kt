package com.saifee.inventorysalesmanagement.mvvm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ActivityLogDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.CompanyDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.CustomerDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.InventoryLogDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.ItemGroupDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.LocationDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PaymentReceivedDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PaymentsOutDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PurchaseBatchDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PurchaseDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.PurchaseItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesReturnDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.SalesReturnItemDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.dao.VendorDao
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ActivityLogEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.InventoryLogEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemGroupEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.LocationEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentReceivedEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PaymentsOutEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseBatchEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.PurchaseItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesReturnItemEntity
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.VendorEntity

@Database(
    entities = [
        ItemEntity::class,
        ItemGroupEntity::class,
        SalesEntity::class,
        SalesItemEntity::class,
        PurchaseEntity::class,
        PurchaseItemEntity::class,
        PurchaseBatchEntity::class,
        CustomerEntity::class,
        VendorEntity::class,
        PaymentReceivedEntity::class,
        PaymentsOutEntity::class,
        SalesReturnEntity::class,
        SalesReturnItemEntity::class,
        InventoryLogEntity::class,
        ActivityLogEntity::class,
        CompanyEntity::class,
        LocationEntity::class
    ], version = 6, exportSchema = true
)
abstract class DataBase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun itemGroupDao(): ItemGroupDao
    abstract fun salesDao(): SalesDao
    abstract fun salesItemDao(): SalesItemDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun purchaseItemDao(): PurchaseItemDao
    abstract fun purchaseBatchDao(): PurchaseBatchDao
    abstract fun customerDao(): CustomerDao
    abstract fun vendorDao(): VendorDao
    abstract fun paymentReceivedDao(): PaymentReceivedDao
    abstract fun paymentsOutDao(): PaymentsOutDao
    abstract fun salesReturnDao(): SalesReturnDao
    abstract fun salesReturnItemDao(): SalesReturnItemDao
    abstract fun inventoryLogDao(): InventoryLogDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun companyDao(): CompanyDao
    abstract fun locationDao(): LocationDao
}
