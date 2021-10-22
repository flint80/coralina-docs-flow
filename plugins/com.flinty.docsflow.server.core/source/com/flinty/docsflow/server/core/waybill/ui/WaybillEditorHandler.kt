/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.waybill.ui

import com.flinty.docsflow.common.core.model.domain.Waybill
import com.flinty.docsflow.common.core.model.ui.*
import com.flinty.docsflow.web.core.waybill.WaybillItemsTableEditorVM
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import kotlin.reflect.KClass

class WaybillEditorHandler:ObjectEditorHandler<Waybill, WaybillEditorVM, WaybillEditorVS, WaybillEditorVV>{
    override fun getObjectClass(): KClass<Waybill> {
        return Waybill::class
    }

    override fun getVMClass(): KClass<WaybillEditorVM> {
        return WaybillEditorVM::class
    }

    override fun getVSClass(): KClass<WaybillEditorVS> {
        return WaybillEditorVS::class
    }

    override fun getVVClass(): KClass<WaybillEditorVV> {
        return WaybillEditorVV::class
    }

    override fun fillSettings(entity: Waybill, vsEntity: WaybillEditorVS, vmEntity: WaybillEditorVM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    override fun read(entity: Waybill, vmEntity: WaybillEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.number = entity.number
        vmEntity.invoice = entity.invoice
        vmEntity.status = entity.status
        vmEntity.supplier = entity.supplier
        vmEntity.positions = WaybillItemsTableEditorVM()
        entity.positions.forEach {pos ->
            vmEntity.positions.items.add(WaybillPositionsTableItemEditorVM().also {
                it.article = pos.article
                it.amount = pos.amount
                it.invoiceAmount = pos.invoiceAmount
                it.name = pos.name
                it.unit = pos.unit
                it.storeAmount = pos.storeAmount
                it.position = pos.positionNumber
                pos.specificationsSplit.forEach { sp ->
                    it.specificationSplits.add(WaybillSpecificationSplitVM().also { spVM ->
                        spVM.uid = sp.uid
                        spVM.amount = sp.amount
                        spVM.specification = sp.specification
                    })
                }
            })
        }
    }

    override fun getTitle(entity: Waybill, vmEntity: WaybillEditorVM, vsEntity: WaybillEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.number
    }

    override fun write(entity: Waybill, vmEntity: WaybillEditorVM, ctx: MutableMap<String, Any?>) {
        entity.number = vmEntity.number
        entity.status = vmEntity.status!!
        vmEntity.positions.items.forEach {vmPos ->
            val pos = entity.positions.find { it.article == vmPos.article }!!
            pos.amount = vmPos.amount!!
            pos.positionNumber = vmPos.position
        }
    }

    override fun validate(vmEntity: WaybillEditorVM, vvEntity: WaybillEditorVV, ctx: MutableMap<String, Any?>) {
        if(vmEntity.status == null){
            vvEntity.status = StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(vmEntity.number)){
            vvEntity.number = StandardL10nMessagesFactory.Empty_field()
        }
    }
}