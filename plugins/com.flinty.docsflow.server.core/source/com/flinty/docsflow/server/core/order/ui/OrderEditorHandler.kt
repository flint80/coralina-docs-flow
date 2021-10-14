/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.order.ui

import com.flinty.docsflow.common.core.model.domain.OrderPosition
import com.flinty.docsflow.common.core.model.domain.OrderSpecificationSplit
import com.flinty.docsflow.common.core.model.domain.SpecificationOrder
import com.flinty.docsflow.common.core.model.ui.*
import com.flinty.docsflow.web.core.order.OrderItemsTableEditorVM
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import kotlin.reflect.KClass

class OrderEditorHandler:ObjectEditorHandler<SpecificationOrder, OrderEditorVM, OrderEditorVS, OrderEditorVV>{
    override fun getObjectClass(): KClass<SpecificationOrder> {
        return SpecificationOrder::class
    }

    override fun getVMClass(): KClass<OrderEditorVM> {
        return OrderEditorVM::class
    }

    override fun getVSClass(): KClass<OrderEditorVS> {
        return OrderEditorVS::class
    }

    override fun getVVClass(): KClass<OrderEditorVV> {
        return OrderEditorVV::class
    }

    override fun fillSettings(entity: SpecificationOrder, vsEntity: OrderEditorVS, vmEntity: OrderEditorVM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    override fun read(entity: SpecificationOrder, vmEntity: OrderEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.code = entity.code
        vmEntity.status = entity.status
        vmEntity.supplier = entity.supplier
        vmEntity.positions = OrderItemsTableEditorVM()
        entity.positions.forEach {pos ->
            vmEntity.positions.items.add(OrderPositionsTableItemEditorVM().also {
                it.amount = pos.amount
                it.article = pos.article
                it.name = pos.name
                it.specAmount = pos.specificationSplits.sumOf { it.amount }
                it.unit = pos.unit
                it.specificationSplits.addAll(pos.specificationSplits.map {sp ->
                    OrderSpecificationSplitVM().also { spVm->
                        spVm.uid = sp.uid
                        spVm.amount = sp.amount
                        spVm.specification = sp.specification
                        spVm.unit = it.unit
                    }
                })
            })
        }
    }

    override fun getTitle(entity: SpecificationOrder, vmEntity: OrderEditorVM, vsEntity: OrderEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.code
    }

    override fun write(entity: SpecificationOrder, vmEntity: OrderEditorVM, ctx: MutableMap<String, Any?>) {
        entity.status = vmEntity.status!!
        val oldPositions = ArrayList(entity.positions)
        entity.positions.clear()
        vmEntity.positions.items.forEach {vmPos ->
            val pos = oldPositions.find { it.article == vmPos.article }?:OrderPosition().also { it.uid =TextUtils.generateUid() }
            entity.positions.add(pos)
            pos.amount = vmPos.amount!!
            pos.article = vmPos.article!!
            pos.name = vmPos.name!!
            pos.unit = vmPos.unit!!
            val oldSpecSplits = ArrayList(pos.specificationSplits)
            pos.specificationSplits.clear()
            pos.specificationSplits.addAll(vmPos.specificationSplits.map { vmSplit ->
                val split = oldSpecSplits.find { it.specification == vmSplit.specification }?: OrderSpecificationSplit().also {
                    it.uid = TextUtils.generateUid()
                }
                split.amount = vmSplit.amount!!
                split.specification =vmSplit.specification!!
                split
            })
        }
    }

    override fun validate(vmEntity: OrderEditorVM, vvEntity: OrderEditorVV, ctx: MutableMap<String, Any?>) {
        if(vmEntity.status == null){
            vvEntity.status = StandardL10nMessagesFactory.Empty_field()
        }
    }
}