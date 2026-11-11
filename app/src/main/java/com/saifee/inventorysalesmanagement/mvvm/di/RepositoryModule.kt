package com.saifee.inventorysalesmanagement.mvvm.di

import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.saifee.inventorysalesmanagement.mvvm.core.common_utils.LocalFileHelper
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import com.saifee.inventorysalesmanagement.mvvm.data.local.DataBase
import com.saifee.inventorysalesmanagement.mvvm.data.repository.ActivityRepositoryImpl
import com.saifee.inventorysalesmanagement.mvvm.data.repository.AuditRepositoryImpl
import com.saifee.inventorysalesmanagement.mvvm.data.repository.AuthRepositoryImpl
import com.saifee.inventorysalesmanagement.mvvm.data.repository.ItemRepositoryImpl
import com.saifee.inventorysalesmanagement.mvvm.data.repository.SyncRepositoryImpl
import com.saifee.inventorysalesmanagement.mvvm.data.repository.TenantRepositoryImpl
import com.saifee.inventorysalesmanagement.mvvm.data.repository.TradingRepositoryImpl
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ActivityRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuditRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.SyncRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TenantRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.TradingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTenantRepository(impl: TenantRepositoryImpl): TenantRepository

    @Binds
    @Singleton
    abstract fun bindItemRepository(impl: ItemRepositoryImpl): ItemRepository

    @Binds
    @Singleton
    abstract fun bindTradingRepository(impl: TradingRepositoryImpl): TradingRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(impl: ActivityRepositoryImpl): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAuditRepository(impl: AuditRepositoryImpl): AuditRepository

    @Binds
    @Singleton
    abstract fun bindUserManagementRepository(impl: com.saifee.inventorysalesmanagement.mvvm.data.repository.UserManagementRepositoryImpl): com.saifee.inventorysalesmanagement.mvvm.domain.repository.UserManagementRepository

    @Module
    @InstallIn(SingletonComponent::class)
    object ProvidesModule {
        @Provides
        @Singleton
        fun provideSyncRepository(
            db: DataBase,
            prefs: SharedPref,
            workManager: WorkManager,
            firestore: FirebaseFirestore,
            storage: FirebaseStorage,
            localFileHelper: LocalFileHelper
        ): SyncRepository {
            return SyncRepositoryImpl(db, prefs, workManager, firestore, storage, localFileHelper)
        }
    }
}
