package com.saifee.inventorysalesmanagement.core.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.saifee.inventorysalesmanagement.mvvm.domain.model.InvoiceDetail
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGenerator {

    fun generateInvoicePdf(invoice: InvoiceDetail, outputFile: File) {
        val pdfDocument = PdfDocument()
        var pageNum = 1
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create() // A4 Size
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // 1. Header - Business Info
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 20f
        paint.color = Color.BLACK
        canvas.drawText(invoice.company.companyName, 40f, 50f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        var yPos = 70f
        invoice.company.address?.let {
            canvas.drawText(it, 40f, yPos, paint)
            yPos += 15f
        }
        invoice.company.phone?.let {
            canvas.drawText("Phone: $it", 40f, yPos, paint)
            yPos += 15f
        }
        invoice.company.email?.let {
            canvas.drawText("Email: $it", 40f, yPos, paint)
            yPos += 15f
        }

        // 2. Invoice Meta Info (Top Right)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("INVOICE", 555f, 50f, paint)
        canvas.drawText("No: ${invoice.sale.invoiceNumber}", 555f, 65f, paint)
        canvas.drawText("Date: ${dateFormat.format(Date(invoice.sale.createdAt))}", 555f, 80f, paint)
        paint.textAlign = Paint.Align.LEFT

        // 3. Billing Address
        yPos += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("BILL TO:", 40f, yPos, paint)
        yPos += 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        invoice.customer?.let {
            canvas.drawText(it.name, 40f, yPos, paint)
            yPos += 15f
            it.address?.let { addr ->
                canvas.drawText(addr, 40f, yPos, paint)
                yPos += 15f
            }
            it.phone?.let { ph ->
                canvas.drawText("Phone: $ph", 40f, yPos, paint)
                yPos += 15f
            }
        } ?: run {
            canvas.drawText("Cash Customer", 40f, yPos, paint)
            yPos += 15f
        }

        // Helper function to draw table header
        fun drawTableHeader(currentY: Float): Float {
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawRect(40f, currentY, 555f, currentY + 25f, paint.apply { color = Color.LTGRAY; style = Paint.Style.FILL })
            paint.color = Color.BLACK
            canvas.drawText("Item Description", 45f, currentY + 17f, paint)
            canvas.drawText("Qty", 350f, currentY + 17f, paint)
            canvas.drawText("Price", 420f, currentY + 17f, paint)
            canvas.drawText("Amount", 500f, currentY + 17f, paint)
            return currentY + 25f
        }

        // 4. Items Table Header
        yPos += 20f
        yPos = drawTableHeader(yPos)

        // 5. Items Rows
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        invoice.items.forEach { item ->
            if (yPos > 720f) {
                // Finish current page and start a new page
                pdfDocument.finishPage(page)
                pageNum++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = drawTableHeader(40f)
            }

            yPos += 20f
            canvas.drawText(item.itemName, 45f, yPos, paint)
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", item.quantity), 350f, yPos, paint)
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", item.price), 420f, yPos, paint)
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", item.total), 500f, yPos, paint)
            
            // Border at bottom of row
            canvas.drawLine(40f, yPos + 5f, 555f, yPos + 5f, paint.apply { strokeWidth = 0.5f; color = Color.GRAY })
            paint.color = Color.BLACK
            yPos += 5f
        }

        if (yPos > 700f) {
            pdfDocument.finishPage(page)
            pageNum++
            pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPos = 40f
        }

        // 6. Summary / Totals
        yPos += 40f
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Subtotal:", 480f, yPos, paint)
        canvas.drawText(String.format(Locale.getDefault(), "%.2f", invoice.sale.totalAmount), 555f, yPos, paint)
        yPos += 15f
        canvas.drawText("Tax:", 480f, yPos, paint)
        canvas.drawText(String.format(Locale.getDefault(), "%.2f", invoice.sale.tax), 555f, yPos, paint)
        yPos += 15f
        canvas.drawText("Discount:", 480f, yPos, paint)
        canvas.drawText(String.format(Locale.getDefault(), "%.2f", invoice.sale.discount), 555f, yPos, paint)
        yPos += 20f
        
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        canvas.drawText("Total:", 480f, yPos, paint)
        canvas.drawText("₹" + String.format(Locale.getDefault(), "%.2f", invoice.sale.finalAmount), 555f, yPos, paint)

        // 7. Footer
        yPos = 800f
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("Thank you for your business!", 297f, yPos, paint)

        pdfDocument.finishPage(page)
        
        try {
            pdfDocument.writeTo(FileOutputStream(outputFile))
        } finally {
            pdfDocument.close()
        }
    }
}
