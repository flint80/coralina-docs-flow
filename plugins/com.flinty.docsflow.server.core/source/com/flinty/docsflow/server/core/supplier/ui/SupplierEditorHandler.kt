/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.supplier.ui

import com.flinty.docsflow.common.core.model.domain.Supplier
import com.flinty.docsflow.common.core.model.domain.SupplierType
import com.flinty.docsflow.common.core.model.ui.SpellVariantVM
import com.flinty.docsflow.common.core.model.ui.SupplierEditorVM
import com.flinty.docsflow.common.core.model.ui.SupplierEditorVS
import com.flinty.docsflow.common.core.model.ui.SupplierEditorVV
import com.gridnine.jasmine.common.core.model.TextBoxConfiguration
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import kotlin.reflect.KClass

class SupplierEditorHandler:ObjectEditorHandler<Supplier, SupplierEditorVM, SupplierEditorVS, SupplierEditorVV>{
    override fun getObjectClass(): KClass<Supplier> {
        return Supplier::class
    }

    override fun getVMClass(): KClass<SupplierEditorVM> {
        return SupplierEditorVM::class
    }

    override fun getVSClass(): KClass<SupplierEditorVS> {
        return SupplierEditorVS::class
    }

    override fun getVVClass(): KClass<SupplierEditorVV> {
        return SupplierEditorVV::class
    }

    override fun fillSettings(entity: Supplier, vsEntity: SupplierEditorVS, vmEntity: SupplierEditorVM, ctx: MutableMap<String, Any?>) {
    }
    override fun read(entity: Supplier, vmEntity: SupplierEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.name = entity.name
        vmEntity.type = entity.type
        vmEntity.spellVariants.addAll(entity.spellVariants.withIndex().map { value -> SpellVariantVM().also {
            it.uid = "${entity.uid}_${value.index}"
            it.variant = value.value
        } })
    }

    override fun getTitle(entity: Supplier, vmEntity: SupplierEditorVM, vsEntity: SupplierEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.name
    }

    override fun write(entity: Supplier, vmEntity: SupplierEditorVM, ctx: MutableMap<String, Any?>) {
        entity.name = vmEntity.name
        entity.type = vmEntity.type?:SupplierType.STANDARD
        entity.spellVariants.clear()
        entity.spellVariants.addAll(vmEntity.spellVariants.mapNotNull { it.variant })
    }

    override fun validate(vmEntity: SupplierEditorVM, vvEntity: SupplierEditorVV, ctx: MutableMap<String, Any?>) {
        if(TextUtils.isBlank(vmEntity.name)){
            vvEntity.name = StandardL10nMessagesFactory.Empty_field()
        }
    }
}