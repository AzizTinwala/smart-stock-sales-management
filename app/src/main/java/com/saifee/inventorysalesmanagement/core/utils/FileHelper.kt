package com.saifee.inventorysalesmanagement.core.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class FileHelper(private val context: Context) {

    fun getFileUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun getInvoiceCacheFile(invoiceNumber: String): File {
        val cacheDir = File(context.cacheDir, "invoices")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        return File(cacheDir, "Invoice_$invoiceNumber.pdf")
    }

    fun clearInvoiceCache() {
        File(context.cacheDir, "invoices").deleteRecursively()
    }
}
