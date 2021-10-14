/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.invoice.ui

import com.flinty.docsflow.common.core.model.domain.Invoice
import com.flinty.docsflow.common.core.model.ui.*
import com.flinty.docsflow.web.core.invoice.InvoiceItemsTableEditorVM
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import kotlin.reflect.KClass

class InvoiceEditorHandler:ObjectEditorHandler<Invoice, InvoiceEditorVM, InvoiceEditorVS, InvoiceEditorVV>{
    override fun getObjectClass(): KClass<Invoice> {
        return Invoice::class
    }

    override fun getVMClass(): KClass<InvoiceEditorVM> {
        return InvoiceEditorVM::class
    }

    override fun getVSClass(): KClass<InvoiceEditorVS> {
        return InvoiceEditorVS::class
    }

    override fun getVVClass(): KClass<InvoiceEditorVV> {
        return InvoiceEditorVV::class
    }

    override fun fillSettings(entity: Invoice, vsEntity: InvoiceEditorVS, vmEntity: InvoiceEditorVM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    override fun read(entity: Invoice, vmEntity: InvoiceEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.number = entity.number
        vmEntity.order = entity.order
        vmEntity.status = entity.status
        vmEntity.supplier = entity.supplier
        vmEntity.positions = InvoiceItemsTableEditorVM()
        vmEntity.vatIncluded = entity.vatIncluded
        entity.positions.forEach {pos ->
            vmEntity.positions.items.add(InvoicePositionsTableItemEditorVM().also {
                it.article = pos.article
                it.invoiceAmount = pos.invoiceAmount
                it.name= pos.name
                it.orderAmount = pos.orderAmount
                it.surplus = pos.surplus
                it.totalPrice = pos.totalPrice
                it.unit = pos.unit
                pos.surplusSplits.forEach { sp ->
                    it.orderSplits.add(InvoiceSurplusSplitVM().also { spVM ->
                        spVM.uid = sp.uid
                        spVM.amount = sp.amount
                        spVM.order = sp.order
                    })
                }
            })
        }
    }

    override fun getTitle(entity: Invoice, vmEntity: InvoiceEditorVM, vsEntity: InvoiceEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.number
    }

    override fun write(entity: Invoice, vmEntity: InvoiceEditorVM, ctx: MutableMap<String, Any?>) {
        entity.number = vmEntity.number
        entity.status = vmEntity.status!!
        vmEntity.positions.items.forEach {vmPos ->
            val pos = entity.positions.find { it.article == vmPos.article }!!
            pos.invoiceAmount = vmPos.invoiceAmount
            pos.totalPrice = vmPos.totalPrice
        }
    }

    override fun validate(vmEntity: InvoiceEditorVM, vvEntity: InvoiceEditorVV, ctx: MutableMap<String, Any?>) {
        if(vmEntity.status == null){
            vvEntity.status = StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(vmEntity.number)){
            vvEntity.number = StandardL10nMessagesFactory.Empty_field()
        }
    }
}