package com.saifee.inventorysalesmanagement.mvvm.core.pref

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.saifee.inventorysalesmanagement.core.enums.AppTheme

class SharedPref(context: Context) {

    private val prefs: SharedPreferences = createEncryptedSharedPreferences(context)

    companion object {
        private const val TAG = "SharedPref"
        private const val PREF_FILE_NAME = "sync_prefs"

        @Suppress("DEPRECATION")
        private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
            return try {
                createEncryptedPrefsInstance(context)
            } catch (e: Exception) {
                Log.e(TAG, "EncryptedSharedPreferences initialization failed, attempting recovery by resetting preference storage", e)
                try {
                    context.deleteSharedPreferences(PREF_FILE_NAME)
                    createEncryptedPrefsInstance(context)
                } catch (recoveryException: Exception) {
                    Log.e(TAG, "Fatal: Unable to initialize secure EncryptedSharedPreferences storage", recoveryException)
                    throw IllegalStateException("Secure encrypted storage unavailable. Unencrypted fallback is disabled for security compliance.", recoveryException)
                }
            }
        }

        private fun createEncryptedPrefsInstance(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                PREF_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    var appTheme: AppTheme
        get() {
            val themeName = prefs.getString("appTheme", AppTheme.SYSTEM.name)
            return try {
                AppTheme.valueOf(themeName ?: AppTheme.SYSTEM.name)
            } catch (e: IllegalArgumentException) {
                AppTheme.SYSTEM
            }
        }
        set(value) = prefs.edit { putString("appTheme", value.name) }

    var isItemDirty: Boolean
        get() = prefs.getBoolean("isItemDirty", false)
        set(value) = prefs.edit { putBoolean("isItemDirty", value) }

    var isSalesDirty: Boolean
        get() = prefs.getBoolean("isSalesDirty", false)
        set(value) = prefs.edit { putBoolean("isSalesDirty", value) }

    var lastSyncTime: Long
        get() = prefs.getLong("lastSyncTime", 0L)
        set(value) = prefs.edit { putLong("lastSyncTime", value) }

    var selectedCompanyId: String?
        get() = prefs.getString("selectedCompanyId", null)
        set(value) = prefs.edit { putString("selectedCompanyId", value) }

    var selectedLocationId: String?
        get() = prefs.getString("selectedLocationId", null)
        set(value) = prefs.edit { putString("selectedLocationId", value) }

    var defaultRoundOff: Boolean
        get() = prefs.getBoolean("defaultRoundOff", false)
        set(value) = prefs.edit { putBoolean("defaultRoundOff", value) }

    var userId: String?
        get() = prefs.getString("userId", null)
        set(value) = prefs.edit { putString("userId", value) }

    var businessName: String
        get() = prefs.getString("businessName", "Saifee Inventory") ?: ""
        set(value) = prefs.edit { putString("businessName", value) }

    var tagline: String
        get() = prefs.getString("tagline", "Quality Inventory Management") ?: ""
        set(value) = prefs.edit { putString("tagline", value) }

    var gstNumber: String
        get() = prefs.getString("gstNumber", "") ?: ""
        set(value) = prefs.edit { putString("gstNumber", value) }

    var phone: String
        get() = prefs.getString("phone", "") ?: ""
        set(value) = prefs.edit { putString("phone", value) }

    var email: String
        get() = prefs.getString("email", "") ?: ""
        set(value) = prefs.edit { putString("email", value) }

    var address: String
        get() = prefs.getString("address", "") ?: ""
        set(value) = prefs.edit { putString("address", value) }

    var currencySymbol: String
        get() = prefs.getString("currencySymbol", "₹") ?: "₹"
        set(value) = prefs.edit { putString("currencySymbol", value) }

    var defaultTaxRate: Float
        get() = prefs.getFloat("defaultTaxRate", 18.0f)
        set(value) = prefs.edit { putFloat("defaultTaxRate", value) }

    var invoicePrefix: String
        get() = prefs.getString("invoicePrefix", "INV-") ?: "INV-"
        set(value) = prefs.edit { putString("invoicePrefix", value) }

    var isDeviceFrameMode: Boolean
        get() = prefs.getBoolean("isDeviceFrameMode", false)
        set(value) = prefs.edit { putBoolean("isDeviceFrameMode", value) }

    var tokenVersion: Long
        get() = prefs.getLong("tokenVersion", 1L)
        set(value) = prefs.edit { putLong("tokenVersion", value) }

    var isAppLockEnabled: Boolean
        get() = prefs.getBoolean("isAppLockEnabled", false)
        set(value) = prefs.edit { putBoolean("isAppLockEnabled", value) }

    var appLockType: String
        get() = prefs.getString("appLockType", "BIOMETRIC") ?: "BIOMETRIC"
        set(value) = prefs.edit { putString("appLockType", value) }

    var appPinCode: String
        get() = prefs.getString("appPinCode", "") ?: ""
        set(value) = prefs.edit { putString("appPinCode", value) }

    var managerPinHash: String
        get() = prefs.getString("managerPinHash", "") ?: ""
        set(value) = prefs.edit { putString("managerPinHash", value) }

    var subscriptionExpiryTimestamp: Long
        get() = prefs.getLong("subscriptionExpiryTimestamp", System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
        set(value) = prefs.edit { putLong("subscriptionExpiryTimestamp", value) }

    var lastOnlineVerificationTimestamp: Long
        get() = prefs.getLong("lastOnlineVerificationTimestamp", System.currentTimeMillis())
        set(value) = prefs.edit { putLong("lastOnlineVerificationTimestamp", value) }
}