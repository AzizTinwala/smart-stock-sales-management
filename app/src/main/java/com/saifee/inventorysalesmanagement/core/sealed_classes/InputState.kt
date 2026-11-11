package com.saifee.inventorysalesmanagement.core.sealed_classes

sealed class InputState {
    object Normal : InputState()
    object Error : InputState()
    object Success : InputState()
}