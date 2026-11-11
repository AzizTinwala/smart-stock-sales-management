package com.saifee.inventorysalesmanagement.mvvm.presentation.sales.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saifee.inventorysalesmanagement.core.utils.FileHelper
import com.saifee.inventorysalesmanagement.core.utils.PdfGenerator
import com.saifee.inventorysalesmanagement.mvvm.core.sealed_classes.ResultState
import com.saifee.inventorysalesmanagement.mvvm.domain.model.InvoiceDetail
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.SalesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvoiceUiState(
    val isLoading: Boolean = false,
    val invoice: InvoiceDetail? = null,
    val errorMessage: String? = null,
    val pdfFile: java.io.File? = null
)

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val salesUseCases: SalesUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoiceUiState())
    val uiState: StateFlow<InvoiceUiState> = _uiState.asStateFlow()

    private val salesId: String? = savedStateHandle["salesId"]

    init {
        salesId?.let { loadInvoice(it) }
    }

    private fun loadInvoice(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = salesUseCases.getInvoiceDetail(id)) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(isLoading = false, invoice = result.data) }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun generatePdf(context: Context, onComplete: (java.io.File) -> Unit) {
        val invoice = uiState.value.invoice ?: return
        viewModelScope.launch {
            val fileHelper = FileHelper(context)
            val pdfGenerator = PdfGenerator()
            val file = fileHelper.getInvoiceCacheFile(invoice.sale.invoiceNumber)
            pdfGenerator.generateInvoicePdf(invoice, file)
            _uiState.update { it.copy(pdfFile = file) }
            onComplete(file)
        }
    }

    fun shareInvoice(context: Context) {
        generatePdf(context) { file ->
            val fileHelper = FileHelper(context)
            val uri = fileHelper.getFileUri(file)
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Invoice"))
        }
    }
}
