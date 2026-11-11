package com.saifee.inventorysalesmanagement.mvvm.core.helper_class

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    suspend fun post(event: AppEvent) {
        _events.emit(event)
    }

    fun tryPost(event: AppEvent) {
        _events.tryEmit(event)
    }

}