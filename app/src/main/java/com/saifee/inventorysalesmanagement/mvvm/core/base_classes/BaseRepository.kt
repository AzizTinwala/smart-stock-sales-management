package com.saifee.inventorysalesmanagement.mvvm.core.base_classes

import android.util.Log
import com.saifee.inventorysalesmanagement.mvvm.data.remote.interceptor.NoInternetException
import com.saifee.inventorysalesmanagement.mvvm.core.helper_class.NetworkResult
import retrofit2.Response

open class BaseRepository {
    suspend fun <T> safeApiCall(
        tag: String,
        apiCall: suspend () -> Response<T?>?
    ): NetworkResult<T> {
        return try {
            val response = apiCall() ?: throw Exception()
            // setResponseData(tag, response)
            val requests = response.raw().request
            val apiUrl = requests.url.toString()

            Log.e("$tag -> URL", apiUrl)
            Log.e("$tag -> PARAMS", requests.body.toString())
            Log.e("$tag -> Response", response.body().toString())

            if (response.isSuccessful) {
                NetworkResult.Success(response.code(), response.body()!!)
            } else if (response.errorBody() != null) {
                NetworkResult.Error(response.code(), response.message())
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: NoInternetException) {
            e.printStackTrace()
            NetworkResult.Error(502, e.message ?: "No Internet Connection")
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(-1, e.message ?: "Something went wrong")
        }
    }

}