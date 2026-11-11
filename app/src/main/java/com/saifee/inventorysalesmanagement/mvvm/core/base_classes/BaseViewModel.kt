package com.saifee.inventorysalesmanagement.mvvm.core.base_classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    fun <P, T> execute(
        state: MutableStateFlow<UiState<T>>,
        useCase: BaseUseCase<P, T>,
        params: P,
        onSuccess: (T) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            state.value = UiState.Loading

            val result = useCase(params)

            state.value = result.fold(
                onSuccess = { onSuccessData -> 
                    onSuccess(onSuccessData)
                    UiState.Success(onSuccessData) 
                },
                onError = { onErrorData -> 
                    onError(onErrorData)
                    UiState.Error(onErrorData) 
                },
                onLoading = { UiState.Loading }
            )
        }
    }


    // Add this magic helper!
    inline fun <T, R> ResultState<T>.fold(
        onSuccess: (T) -> R,
        onError: (String) -> R,
        onLoading: () -> R
    ): R {
        return when (this) {
            is ResultState.Success -> onSuccess(data)
            is ResultState.Error -> onError(message)
            is ResultState.Loading -> onLoading()
        }
    }
}