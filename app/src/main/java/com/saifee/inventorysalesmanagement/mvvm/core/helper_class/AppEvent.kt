package com.saifee.inventorysalesmanagement.mvvm.core.helper_class

sealed class AppEvent {
    data class ShowSnackBar(val message: String) : AppEvent()
    object NoInternet : AppEvent()
    object Logout : AppEvent()
}