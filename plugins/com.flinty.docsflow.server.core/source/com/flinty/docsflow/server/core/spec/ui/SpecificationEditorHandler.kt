/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.spec.ui

import com.flinty.docsflow.common.core.model.domain.Specification
import com.flinty.docsflow.common.core.model.domain.SpecificationPosition
import com.flinty.docsflow.common.core.model.domain.Supplier
import com.flinty.docsflow.common.core.model.domain.SupplierType
import com.flinty.docsflow.common.core.model.ui.*
import com.flinty.docsflow.server.core.spec.common.SpecificationUtils
import com.flinty.docsflow.web.core.spec.SpecificationItemsTableEditorVM
import com.gridnine.jasmine.common.core.model.GeneralSelectBoxConfiguration
import com.gridnine.jasmine.common.core.model.TextBoxConfiguration
import com.gridnine.jasmine.common.core.utils.CommonUiUtils
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import kotlin.reflect.KClass

class SpecificationEditorHandler:ObjectEditorHandler<Specification, SpecificationEditorVM, SpecificationEditorVS, SpecificationEditorVV>{
    override fun getObjectClass(): KClass<Specification> {
        return Specification::class
    }

    override fun getVMClass(): KClass<SpecificationEditorVM> {
        return SpecificationEditorVM::class
    }

    override fun getVSClass(): KClass<SpecificationEditorVS> {
        return SpecificationEditorVS::class
    }

    override fun getVVClass(): KClass<SpecificationEditorVV> {
        return SpecificationEditorVV::class
    }

    override fun fillSettings(entity: Specification, vsEntity: SpecificationEditorVS, vmEntity: SpecificationEditorVM, ctx: MutableMap<String, Any?>) {
        vsEntity.project = SpecificationUtils.getProjectSelectConfiguration().also {
            it.notEditable = true
        }
    }

    override fun read(entity: Specification, vmEntity: SpecificationEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.name = entity.name
        vmEntity.code = entity.code
        vmEntity.project = CommonUiUtils.toSelectItem(entity.project)
        vmEntity.status = entity.status
        vmEntity.specification = SpecificationItemsTableEditorVM()
        entity.positions.forEach {pos ->
            vmEntity.specification.items.add(SpecificationItemsTableItemEditorVM().also {
                it.amount = pos.amount
                it.amountNote = pos.amountNote
                it.article = pos.article
                it.name = pos.name
                it.storeAmount = pos.storeAmount
                it.supplier = pos.supplier
                it.toBeOrdered = pos.toBeOrdered
                it.unit = pos.unit
                it.unitNote = pos.unitNote
                it.order = pos.order
            })
        }
    }

    override fun getTitle(entity: Specification, vmEntity: SpecificationEditorVM, vsEntity: SpecificationEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.code
    }

    override fun write(entity: Specification, vmEntity: SpecificationEditorVM, ctx: MutableMap<String, Any?>) {
        entity.name = vmEntity.name!!
        val oldPositions = ArrayList(entity.positions)
        entity.positions.clear()
        vmEntity.specification.items.forEach {vmPos ->
            val pos = oldPositions.find { it.article == vmPos.article }?:SpecificationPosition().also { it.uid = TextUtils.generateUid() }
            entity.positions.add(pos)
            pos.unitNote = vmPos.unitNote
            pos.unit = vmPos.unit!!
            pos.toBeOrdered = vmPos.toBeOrdered!!
            pos.supplier = vmPos.supplier!!
            pos.name = vmPos.name!!
            pos.amount = vmPos.amount!!
            pos.storeAmount = vmPos.amount!!.subtract(vmPos.storeAmount)
            pos.article = vmPos.article!!
            pos.amountNote = vmPos.amountNote
        }
    }

    override fun validate(vmEntity: SpecificationEditorVM, vvEntity: SpecificationEditorVV, ctx: MutableMap<String, Any?>) {
        if(TextUtils.isBlank(vmEntity.name)){
            vvEntity.name = StandardL10nMessagesFactory.Empty_field()
        }
    }
}