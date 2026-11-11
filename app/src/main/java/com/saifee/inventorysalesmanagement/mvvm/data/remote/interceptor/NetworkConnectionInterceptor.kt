package com.saifee.inventorysalesmanagement.mvvm.data.remote.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.saifee.inventorysalesmanagement.mvvm.core.helper_class.AppEvent
import com.saifee.inventorysalesmanagement.mvvm.core.helper_class.AppEventBus
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import javax.inject.Inject

class NetworkConnectionInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appEventBus: AppEventBus
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        if (!isConnected()) {
            appEventBus.tryPost(AppEvent.NoInternet)
            throw NoInternetException("No Internet Connection")
        }
        return chain.proceed(chain.request().newBuilder().build())
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

class NoInternetException(message: String) : IOException(message)
