package com.saifee.inventorysalesmanagement.mvvm.core.enums

enum class ActivityLogActions {
    ITEM_ADDED,
    ITEM_DELETED,
    SALE_CREATED,
    CUSTOMER_ADDED;

    val type: String
        get() = name
}