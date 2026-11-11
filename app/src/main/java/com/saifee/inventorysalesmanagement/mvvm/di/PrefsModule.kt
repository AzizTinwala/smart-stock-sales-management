package com.saifee.inventorysalesmanagement.mvvm.di

import android.content.Context
import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefsModule {
    @Provides
    @Singleton
    fun provideSyncPrefs(@ApplicationContext context: Context): SharedPref {
        return SharedPref(context)
    }
}