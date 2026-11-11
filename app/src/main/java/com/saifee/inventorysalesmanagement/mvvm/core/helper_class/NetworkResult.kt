package com.saifee.inventorysalesmanagement.mvvm.core.helper_class

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null, val code: Int? = null,) {
    class Success<T>(code:Int,data: T) : NetworkResult<T>(data, code=code)
    class Error<T>(code:Int,message: String?,data: T?=null) : NetworkResult<T>(message = message, code = code, data = data)
    class Loading<T> : NetworkResult<T>()
}