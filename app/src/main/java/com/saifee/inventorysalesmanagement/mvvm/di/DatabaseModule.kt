package com.saifee.inventorysalesmanagement.mvvm.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.saifee.inventorysalesmanagement.mvvm.data.local.DataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Re-create sales_item table with TEXT salesItemId PK and RESTRICT FK on item
            db.execSQL("CREATE TABLE IF NOT EXISTS `sales_item_new` (`salesItemId` TEXT NOT NULL, `companyId` TEXT NOT NULL, `locationId` TEXT NOT NULL, `salesId` TEXT NOT NULL, `itemId` INTEGER NOT NULL, `quantity` REAL NOT NULL, `price` REAL NOT NULL, `total` REAL NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `createdBy` TEXT NOT NULL, `updatedBy` TEXT NOT NULL, `isSynced` INTEGER NOT NULL, PRIMARY KEY(`salesItemId`), FOREIGN KEY(`salesId`) REFERENCES `sales`(`salesId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`itemId`) REFERENCES `item`(`itemId`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
            db.execSQL("INSERT INTO `sales_item_new` SELECT CAST(`salesItemId` AS TEXT), `companyId`, `locationId`, `salesId`, `itemId`, `quantity`, `price`, `total`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isSynced` FROM `sales_item` ")
            db.execSQL("DROP TABLE `sales_item` ")
            db.execSQL("ALTER TABLE `sales_item_new` RENAME TO `sales_item` ")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_item_salesId` ON `sales_item` (`salesId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_item_itemId` ON `sales_item` (`itemId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_item_companyId` ON `sales_item` (`companyId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_item_locationId` ON `sales_item` (`locationId`)")

            // Re-create purchase_item table with TEXT purchaseItemId PK and RESTRICT FK on item
            db.execSQL("CREATE TABLE IF NOT EXISTS `purchase_item_new` (`purchaseItemId` TEXT NOT NULL, `companyId` TEXT NOT NULL, `locationId` TEXT NOT NULL, `purchaseId` TEXT NOT NULL, `itemId` INTEGER NOT NULL, `quantity` REAL NOT NULL, `price` REAL NOT NULL, `total` REAL NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `createdBy` TEXT NOT NULL, `updatedBy` TEXT NOT NULL, `isSynced` INTEGER NOT NULL, PRIMARY KEY(`purchaseItemId`), FOREIGN KEY(`purchaseId`) REFERENCES `purchase`(`purchaseId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`itemId`) REFERENCES `item`(`itemId`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
            db.execSQL("INSERT INTO `purchase_item_new` SELECT CAST(`purchaseItemId` AS TEXT), `companyId`, `locationId`, `purchaseId`, `itemId`, `quantity`, `price`, `total`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isSynced` FROM `purchase_item` ")
            db.execSQL("DROP TABLE `purchase_item` ")
            db.execSQL("ALTER TABLE `purchase_item_new` RENAME TO `purchase_item` ")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchase_item_purchaseId` ON `purchase_item` (`purchaseId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchase_item_itemId` ON `purchase_item` (`itemId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchase_item_companyId` ON `purchase_item` (`companyId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchase_item_locationId` ON `purchase_item` (`locationId`)")

            // Re-create sales_return_item table with TEXT salesReturnItemId PK and RESTRICT FK on item
            db.execSQL("CREATE TABLE IF NOT EXISTS `sales_return_item_new` (`salesReturnItemId` TEXT NOT NULL, `companyId` TEXT NOT NULL, `locationId` TEXT NOT NULL, `salesReturnId` TEXT NOT NULL, `itemId` INTEGER NOT NULL, `quantity` REAL NOT NULL, `price` REAL NOT NULL, `itemCondition` TEXT NOT NULL, `restockAction` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, `createdBy` TEXT NOT NULL, `updatedBy` TEXT NOT NULL, `isSynced` INTEGER NOT NULL, PRIMARY KEY(`salesReturnItemId`), FOREIGN KEY(`salesReturnId`) REFERENCES `sales_return`(`salesReturnId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`itemId`) REFERENCES `item`(`itemId`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
            db.execSQL("INSERT INTO `sales_return_item_new` SELECT CAST(`salesReturnItemId` AS TEXT), `companyId`, `locationId`, `salesReturnId`, `itemId`, `quantity`, `price`, `itemCondition`, `restockAction`, `updatedAt`, `createdBy`, `updatedBy`, `isSynced` FROM `sales_return_item` ")
            db.execSQL("DROP TABLE `sales_return_item` ")
            db.execSQL("ALTER TABLE `sales_return_item_new` RENAME TO `sales_return_item` ")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_return_item_salesReturnId` ON `sales_return_item` (`salesReturnId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_return_item_itemId` ON `sales_return_item` (`itemId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_return_item_companyId` ON `sales_return_item` (`companyId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_return_item_locationId` ON `sales_return_item` (`locationId`)")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DataBase {
        return Room.databaseBuilder(
            context,
            DataBase::class.java,
            "inventory_db"
        ).addMigrations(MIGRATION_5_6).build()
    }

    @Provides fun provideItemDao(db: DataBase) = db.itemDao()
    @Provides fun provideItemGroupDao(db: DataBase) = db.itemGroupDao()
    @Provides fun provideSalesDao(db: DataBase) = db.salesDao()
    @Provides fun provideSalesItemDao(db: DataBase) = db.salesItemDao()
    @Provides fun providePurchaseDao(db: DataBase) = db.purchaseDao()
    @Provides fun providePurchaseItemDao(db: DataBase) = db.purchaseItemDao()
    @Provides fun providePurchaseBatchDao(db: DataBase) = db.purchaseBatchDao()
    @Provides fun provideCustomerDao(db: DataBase) = db.customerDao()
    @Provides fun provideVendorDao(db: DataBase) = db.vendorDao()
    @Provides fun providePaymentReceivedDao(db: DataBase) = db.paymentReceivedDao()
    @Provides fun providePaymentsOutDao(db: DataBase) = db.paymentsOutDao()
    @Provides fun provideSalesReturnDao(db: DataBase) = db.salesReturnDao()
    @Provides fun provideSalesReturnItemDao(db: DataBase) = db.salesReturnItemDao()
    @Provides fun provideInventoryLogDao(db: DataBase) = db.inventoryLogDao()
    @Provides fun provideActivityLogDao(db: DataBase) = db.activityLogDao()
    @Provides fun provideCompanyDao(db: DataBase) = db.companyDao()
    @Provides fun provideLocationDao(db: DataBase) = db.locationDao()
}