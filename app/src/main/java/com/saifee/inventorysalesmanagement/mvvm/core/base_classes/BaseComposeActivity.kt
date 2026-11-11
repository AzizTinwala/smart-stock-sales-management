package com.saifee.inventorysalesmanagement.mvvm.core.base_classes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.saifee.inventorysalesmanagement.mvvm.core.helper_class.AppEvent
import com.saifee.inventorysalesmanagement.mvvm.core.helper_class.AppEventBus
import javax.inject.Inject

abstract class BaseComposeActivity : ComponentActivity() {

    @Inject
    lateinit var appEventBus: AppEventBus

    private val snackbarHostState = SnackbarHostState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BaseActivityContent()
        }
    }

    @Composable
    private fun BaseActivityContent() {

        LaunchedEffect(Unit) {
            appEventBus.events.collect { event ->
                handleEvent(event)
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            ProvideContent(padding)
        }
    }

    @Composable
    abstract fun ProvideContent(padding: PaddingValues)

    private suspend fun handleEvent(event: AppEvent) {
        when (event) {
            is AppEvent.ShowSnackBar -> {
                snackbarHostState.showSnackbar(event.message)
            }
            AppEvent.NoInternet -> {
                snackbarHostState.showSnackbar("No Internet Connection")
            }
            AppEvent.Logout -> {
                // handle logout navigation
            }
        }
    }
}