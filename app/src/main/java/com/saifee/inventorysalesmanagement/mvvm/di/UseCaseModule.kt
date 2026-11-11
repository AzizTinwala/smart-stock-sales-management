package com.saifee.inventorysalesmanagement.mvvm.di

import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.AddCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.DeleteCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.GetCustomerByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.GetCustomersUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.customer.UpdateCustomerUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.inventory.AddInventoryLogUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.inventory.GetInventoryLogsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AddItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.AdjustStockUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.GetItemsByCompanyUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.DeleteItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.GetItemByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.GetItemsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item.UpdateItemUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.AddGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.DeleteGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.GetGroupsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.GetItemGroupByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item_group.UpdateGroupUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.AddPurchaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.DeletePurchaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.GetPurchaseByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.GetPurchasesUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.purchase.UpdatePurchaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.AddSaleUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.DeleteSaleUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.GetInvoiceDetailUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.GetSaleByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.GetSalesUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.sales.UpdateSaleUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.AddVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.DeleteVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.GetVendorByIdUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.GetVendorsUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.vendor.UpdateVendorUseCase
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.CustomerUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.InventoryLogUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemGroupUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.ItemUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.VendorUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.SalesUseCases
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_wrapper.PurchaseUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class) // Only exists while ViewModel lives
object UseCaseModule {

    // ---------- ITEM ----------
    @Provides
    fun provideItemUseCases(
        add: AddItemUseCase,
        update: UpdateItemUseCase,
        delete: DeleteItemUseCase,
        getAll: GetItemsUseCase,
        getById: GetItemByIdUseCase,
        adjustStock: AdjustStockUseCase,
        getByCompany: GetItemsByCompanyUseCase
    ): ItemUseCases {
        return ItemUseCases(add, update, delete, getAll, getById, adjustStock, getByCompany)
    }

    // ---------- GROUP ----------
    @Provides
    fun provideGroupUseCases(
        add: AddGroupUseCase,
        update: UpdateGroupUseCase,
        delete: DeleteGroupUseCase,
        getAll: GetGroupsUseCase,
        getById: GetItemGroupByIdUseCase
    ): ItemGroupUseCases {
        return ItemGroupUseCases(add, update, delete, getAll, getById)
    }

    // ---------- CUSTOMER ----------
    @Provides
    fun provideCustomerUseCases(
        add: AddCustomerUseCase,
        update: UpdateCustomerUseCase,
        delete: DeleteCustomerUseCase,
        getAll: GetCustomersUseCase,
        getById: GetCustomerByIdUseCase
    ): CustomerUseCases {
        return CustomerUseCases(add, update, delete, getAll, getById)
    }

    // ---------- VENDOR ----------
    @Provides
    fun provideVendorUseCases(
        add: AddVendorUseCase,
        update: UpdateVendorUseCase,
        delete: DeleteVendorUseCase,
        getAll: GetVendorsUseCase,
        getById: GetVendorByIdUseCase
    ): VendorUseCases {
        return VendorUseCases(add, update, delete, getAll, getById)
    }

    // ---------- SALES ----------
    @Provides
    fun provideSalesUseCases(
        add: AddSaleUseCase,
        update: UpdateSaleUseCase,
        delete: DeleteSaleUseCase,
        getAll: GetSalesUseCase,
        getById: GetSaleByIdUseCase,
        getInvoiceDetail: GetInvoiceDetailUseCase
    ): SalesUseCases {
        return SalesUseCases(add, update, delete, getAll, getById, getInvoiceDetail)
    }

    // ---------- PURCHASE ----------
    @Provides
    fun providePurchaseUseCases(
        add: AddPurchaseUseCase,
        update: UpdatePurchaseUseCase,
        delete: DeletePurchaseUseCase,
        getAll: GetPurchasesUseCase,
        getById: GetPurchaseByIdUseCase
    ): PurchaseUseCases {
        return PurchaseUseCases(add, update, delete, getAll, getById)
    }


    // ---------- INVENTORY LOG ----------
    @Provides
    fun provideInventoryLogUseCases(
        add: AddInventoryLogUseCase,
        getAll: GetInventoryLogsUseCase
    ): InventoryLogUseCases {
        return InventoryLogUseCases(add, getAll)
    }
}