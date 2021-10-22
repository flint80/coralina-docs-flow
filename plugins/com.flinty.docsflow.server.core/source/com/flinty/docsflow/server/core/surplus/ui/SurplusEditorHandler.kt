/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.surplus.ui

import com.flinty.docsflow.common.core.model.domain.Surplus
import com.flinty.docsflow.common.core.model.ui.SurplusEditorVM
import com.flinty.docsflow.common.core.model.ui.SurplusEditorVS
import com.flinty.docsflow.common.core.model.ui.SurplusEditorVV
import com.flinty.docsflow.common.core.model.ui.SurplusOrderSplitTableVM
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import kotlin.reflect.KClass

class SurplusEditorHandler:ObjectEditorHandler<Surplus, SurplusEditorVM, SurplusEditorVS, SurplusEditorVV>{
    override fun getObjectClass(): KClass<Surplus> {
        return Surplus::class
    }

    override fun getVMClass(): KClass<SurplusEditorVM> {
        return SurplusEditorVM::class
    }

    override fun getVSClass(): KClass<SurplusEditorVS> {
        return SurplusEditorVS::class
    }

    override fun getVVClass(): KClass<SurplusEditorVV> {
        return SurplusEditorVV::class
    }

    override fun fillSettings(entity: Surplus, vsEntity: SurplusEditorVS, vmEntity: SurplusEditorVM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    override fun read(entity: Surplus, vmEntity: SurplusEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.amount = entity.amount
        vmEntity.article = entity.article
        vmEntity.invoice = entity.invoice
        vmEntity.order = entity.order
        vmEntity.status = entity.status
        vmEntity.unit = entity.unit
        vmEntity.name = entity.name
        entity.splits.forEach {pos ->
            vmEntity.splits.add(SurplusOrderSplitTableVM().also {
                it.amount = pos.amount
                it.order = pos.order
            })
        }
    }

    override fun getTitle(entity: Surplus, vmEntity: SurplusEditorVM, vsEntity: SurplusEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.toString()
    }

    override fun write(entity: Surplus, vmEntity: SurplusEditorVM, ctx: MutableMap<String, Any?>) {
    }

    override fun validate(vmEntity: SurplusEditorVM, vvEntity: SurplusEditorVV, ctx: MutableMap<String, Any?>) {
    }
}