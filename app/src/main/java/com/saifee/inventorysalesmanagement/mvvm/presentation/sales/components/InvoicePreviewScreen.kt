package com.saifee.inventorysalesmanagement.mvvm.presentation.sales.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saifee.inventorysalesmanagement.core.ui.components.AppTopBar
import com.saifee.inventorysalesmanagement.core.ui.theme.InventorySalesManagementTheme
import com.saifee.inventorysalesmanagement.mvvm.domain.model.InvoiceDetail
import com.saifee.inventorysalesmanagement.mvvm.domain.model.InvoiceItem
import com.saifee.inventorysalesmanagement.mvvm.presentation.sales.viewmodel.InvoiceViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InvoicePreviewScreen(
    viewModel: InvoiceViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Invoice Preview",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                actions = {
                    IconButton(onClick = { viewModel.shareInvoice(context) }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.invoice != null) {
            InvoiceContent(
                invoice = state.invoice!!,
                modifier = Modifier.padding(padding)
            )
        } else if (state.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun InvoiceContent(
    invoice: InvoiceDetail,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Business Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = invoice.company.companyName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                invoice.company.address?.let { Text(text = it, color = Color.Gray) }
                invoice.company.phone?.let { Text(text = "Phone: $it", color = Color.Gray) }
                invoice.company.email?.let { Text(text = "Email: $it", color = Color.Gray) }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        }

        // Invoice Meta
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "BILL TO:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    invoice.customer?.let {
                        Text(text = it.name, fontWeight = FontWeight.Bold)
                        it.address?.let { addr -> Text(text = addr) }
                        it.phone?.let { ph -> Text(text = "Ph: $ph") }
                    } ?: Text(text = "Cash Customer")
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "INVOICE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = "No: ${invoice.sale.invoiceNumber}")
                    Text(text = "Date: ${dateFormat.format(Date(invoice.sale.createdAt))}")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Table Header
        item {
            Surface(color = Color.LightGray.copy(alpha = 0.3f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Item", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text(text = "Qty", modifier = Modifier.width(50.dp), fontWeight = FontWeight.Bold)
                    Text(text = "Price", modifier = Modifier.width(80.dp), fontWeight = FontWeight.Bold)
                    Text(text = "Amount", modifier = Modifier.width(80.dp), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Items
        items(invoice.items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.itemName, modifier = Modifier.weight(1f))
                Text(text = String.format(Locale.getDefault(), "%.2f", item.quantity), modifier = Modifier.width(50.dp))
                Text(text = String.format(Locale.getDefault(), "%.2f", item.price), modifier = Modifier.width(80.dp))
                Text(text = String.format(Locale.getDefault(), "%.2f", item.total), modifier = Modifier.width(80.dp))
            }
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        }

        // Totals
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.End
            ) {
                TotalRow(label = "Subtotal", value = String.format(Locale.getDefault(), "%.2f", invoice.sale.totalAmount))
                TotalRow(label = "Tax", value = String.format(Locale.getDefault(), "%.2f", invoice.sale.tax))
                TotalRow(label = "Discount", value = String.format(Locale.getDefault(), "%.2f", invoice.sale.discount))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Total:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%.2f", invoice.sale.finalAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Thank you for your business!",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun TotalRow(label: String, value: String) {
    Row(
        modifier = Modifier.width(200.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InvoicePreviewScreenPreview() {
    InventorySalesManagementTheme {
        InvoiceContent(
            invoice = InvoiceDetail(
                sale = com.saifee.inventorysalesmanagement.mvvm.data.local.entity.SalesEntity(
                    salesId = "S1",
                    invoiceNumber = "INV-001",
                    customerId = "C1",
                    totalAmount = 1500.0,
                    discount = 100.0,
                    tax = 150.0,
                    finalAmount = 1550.0,
                    status = "PAID",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    companyId = "CMP1",
                    locationId = "LOC1"
                ),
                items = listOf(
                    InvoiceItem(
                        itemName = "Steel Rods",
                        sku = "ST-001",
                        quantity = 10.0,
                        price = 100.0,
                        total = 1000.0,
                        gstPercentage = 18.0
                    ),
                    InvoiceItem(
                        itemName = "Aluminum Sheets",
                        sku = "AL-002",
                        quantity = 5.0,
                        price = 100.0,
                        total = 500.0,
                        gstPercentage = 18.0
                    )
                ),
                customer = com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CustomerEntity(
                    customerId = "C1",
                    companyId = "CMP1",
                    locationId = "LOC1",
                    name = "Saifee Traders",
                    phone = "9876543210",
                    email = "traders@saifee.com",
                    address = "123 Market St, Mumbai",
                    taxType = "INTRA_STATE",
                    createdAt = 0L,
                    updatedAt = 0L
                ),
                company = com.saifee.inventorysalesmanagement.mvvm.data.local.entity.CompanyEntity(
                    companyId = "CMP1",
                    companyName = "My Business Ltd",
                    address = "456 Industrial Ave, Pune",
                    phone = "011-22223333",
                    email = "contact@mybusiness.com",
                    createdAt = 0L,
                    updatedAt = 0L
                )
            )
        )
    }
}
