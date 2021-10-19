/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.server.core.spec.common.SpecificationUtils
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import com.gridnine.jasmine.common.core.storage.SortOrder
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.server.core.storage.OperationContext
import com.gridnine.jasmine.server.core.storage.StorageInterceptor
import com.gridnine.jasmine.server.standard.model.SequenceNumberGenerator
import java.math.BigDecimal

class SpecificationInterceptor : StorageInterceptor {
    override val priority = 1.0
    override fun <D : BaseDocument> onSave(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is Specification) {
            val newSpecification = doc as Specification
            val specRef = EntityUtils.toReference(newSpecification)
            val oldSpecification = context.globalContext.oldObject as Specification?
            val ordersToCorrect = oldSpecification?.positions?.mapNotNull { it.order }?.distinct() ?: arrayListOf()
            val newItems = ArrayList(newSpecification.positions)
            val surplusses = hashSetOf<Surplus>()
            val invoices = hashSetOf<Invoice>()
            ordersToCorrect.forEach { orderRef ->
                val existingOrder = SpecificationUtils.getOrLoad(orderRef, context)!!
                Storage.get().loadDocument(orderRef, true)!!
                val updated = updateOrder(
                    existingOrder,
                    newItems,
                    specRef,
                    surplusses,
                    invoices,
                    context,
                    existingOrder.status != OrderStatus.DRAFT
                )

                if (updated) {
                    if (existingOrder.positions.isEmpty()) {
                        Storage.get().deleteDocument(existingOrder)
                    } else {
                        Storage.get().saveDocument(existingOrder)
                    }
                }
            }
            val suppliers = newItems.map { it.supplier }.distinct()
            suppliers.forEach { supp ->
                val appropriateOrder =
                    SpecificationUtils.getOrLoad(
                        Storage.get().searchDocuments(SpecificationOrderIndex::class, searchQuery {
                            where {
                                eq(SpecificationOrderIndex.statusProperty, OrderStatus.DRAFT)
                                eq(SpecificationOrderIndex.supplierProperty, supp)
                            }
                        }).firstOrNull()?.document, context
                    ) ?: SpecificationOrder().also {
                        it.status = OrderStatus.DRAFT
                        it.code = "O-${SequenceNumberGenerator.get().incrementAndGet("O")}"
                        it.supplier = supp
                    }
                updateOrder(
                    appropriateOrder,
                    newItems,
                    specRef,
                    surplusses,
                    invoices,
                    context,
                    appropriateOrder.status != OrderStatus.DRAFT
                )
                Storage.get().saveDocument(appropriateOrder)
            }
            surplusses.forEach {
                Storage.get().saveDocument(it)
            }
            invoices.forEach {
                Storage.get().saveDocument(it)
            }
            if (newSpecification.positions.any {
                    val res = it.orderAmount?.compareTo(it.toBeOrdered) != 0
                    res
                }) {
                newSpecification.status = SpecificationStatus.PARTIALLY_ORDERED
            } else {
                newSpecification.status = SpecificationStatus.ORDERED
            }
        }
    }


    override fun <D : BaseDocument> onDelete(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is Specification) {
            val specification = doc as Specification
            val specRef = EntityUtils.toReference(specification)
            val ordersToCorrect = specification.positions.mapNotNull { it.order }.distinct()
            val surplusses = hashSetOf<Surplus>()
            val invoices = hashSetOf<Invoice>()
            ordersToCorrect.forEach { orderRef ->
                val existingOrder = SpecificationUtils.getOrLoad(orderRef, context)!!
                val updated = updateOrder(
                    existingOrder,
                    arrayListOf(),
                    specRef,
                    surplusses,
                    invoices,
                    context,
                    existingOrder.status != OrderStatus.DRAFT
                )
                if (existingOrder.status != OrderStatus.DRAFT && updated) {
                    throw Xeption.forEndUser(L10nMessage("спецификация приводит к изменению заказа ${existingOrder.code}"))
                }
                if (updated) {
                    if (existingOrder.positions.isEmpty()) {
                        Storage.get().deleteDocument(existingOrder)
                    } else {
                        Storage.get().saveDocument(existingOrder)
                    }
                    surplusses.forEach {
                        Storage.get().saveDocument(it)
                    }
                    invoices.forEach {
                        Storage.get().saveDocument(it)
                    }
                }
            }
        }
    }

    private fun <D : BaseDocument> updateOrder(
        order: SpecificationOrder,
        items: ArrayList<SpecificationPosition>,
        specRef: ObjectReference<Specification>,
        surpluses: MutableSet<Surplus>,
        invoices: MutableSet<Invoice>,
        context: OperationContext<D>,
        prohibitModification: Boolean
    ): Boolean {

        var updated = false
        val positionsToDelete = arrayListOf<OrderPosition>()
        val processedItems = arrayListOf<SpecificationPosition>()
        val orderRef = EntityUtils.toReference(order)
        order.positions.forEach { pos ->
            val splitsToDelete = arrayListOf<OrderSpecificationSplit>()
            pos.specificationSplits.forEach { oss ->
                if (oss.specification == specRef) {
                    var newItem = items.find { it.supplier == order.supplier && it.article == pos.article }
                    if(newItem == null){
                        newItem = items.find { it.article == pos.replacementFor }
                        if(newItem != null){
                            newItem.article = pos.replacementFor!!
                            newItem.name = pos.name
                        }
                    }
                    if (newItem == null) {
                        splitsToDelete.add(oss)
                        updated = true
                    } else {
                        if (newItem.unit != pos.unit) {
                            throw Xeption.forEndUser(
                                L10nMessage(
                                    "позиция ${newItem.article} в спецификации имеет единицу измерения " +
                                            "${newItem.unit} отличную от единицы имерения в заказе $order"
                                )
                            )
                        }
                        processedItems.add(newItem)
                        if (oss.amount.compareTo(newItem.toBeOrdered) != 0) {
                            oss.amount = newItem.toBeOrdered
                            newItem.order = orderRef
                            newItem.orderAmount = newItem.toBeOrdered
                            if (prohibitModification) {
                                throw Xeption.forEndUser(L10nMessage("позиция ${newItem.article} спецификации ведет к изменению заказа $order"))
                            }
                            updated = true
                        }
                    }
                }
            }
            pos.specificationSplits.removeAll(splitsToDelete)
            if (pos.specificationSplits.isEmpty()) {
                positionsToDelete.add(pos)
                if (prohibitModification) {
                    throw Xeption.forEndUser(L10nMessage("из заказа $order будет удалена позиция ${pos.article}"))
                }
                updated = true
            }
        }
        order.positions.removeAll(positionsToDelete)
        items.removeAll(processedItems)
        processedItems.clear()
        items.filter { it.supplier == order.supplier }.forEach { item ->
            processedItems.add(item)
            var position = order.positions.find { item.article == it.article }
            if(position == null){
                position = order.positions.find { item.article == it.replacementFor }
                if(position != null){
                    item.article = position.article
                    item.replacementFor = position.replacementFor
                    item.name = position.name
                }
            }
            if(position == null){
                position = OrderPosition().also {
                    it.article = item.article
                    it.name = item.name
                    it.unit = item.unit
                    order.positions.add(it)
                }
            }
            position.specificationSplits.add(OrderSpecificationSplit().also {
                it.amount = item.toBeOrdered
                it.specification = specRef
                item.orderAmount = it.amount
                item.order = orderRef
            })
            if (prohibitModification) {
                throw Xeption.forEndUser(L10nMessage("позиция ${item.article} спецификации ведет к изменению заказа $order"))
            }
            updated = true
        }
        items.removeAll(processedItems)
        if (updated) {
            order.status = OrderStatus.DRAFT
            order.positions.forEach {
                it.surplusSplits.removeIf {
                    val surplus = SpecificationUtils.getOrLoad(it.surplus, context)
                    surplus?.status == SurplusStatus.DRAFT
                }
                val allSurplusses = Storage.get().searchDocuments(SurplusIndex::class, searchQuery {
                    where {
                        eq(SurplusIndex.articleProperty, it.article)
                        eq(SurplusIndex.statusProperty, SurplusStatus.DRAFT)
                    }
                    orderBy(SurplusIndex.nameProperty, SortOrder.ASC)
                }).map { SpecificationUtils.getOrLoad(it.document, context)!! }
                allSurplusses.forEach {
                    if (it.splits.removeIf { it.order == orderRef }) {
                        surpluses.add(it)
                    }
                }
                val invoice = SpecificationUtils.getOrLoad(order.invoice, context)
                invoice?.positions?.forEach {
                    if (it.surplusSplits.removeIf { it.order == orderRef }) {
                        invoices.add(invoice)
                    }
                }

                var amount = it.specificationSplits.sumOf { it.amount }.subtract(it.surplusSplits.sumOf { it.amount })
                if (amount > BigDecimal.ZERO) {
                    allSurplusses.forEach { surplus ->
                        val surplusAmount = surplus.amount.subtract(surplus.splits.sumOf { it.amount })
                        if (surplusAmount > BigDecimal.ZERO) {
                            surpluses.add(surplus)
                            val addedAmount = if (amount > surplusAmount) surplusAmount else amount
                            surplus.splits.add(SurplusSplit().also {
                                it.order = orderRef
                                it.amount = addedAmount
                            })
                            amount = amount.subtract(addedAmount)
                            it.surplusSplits.add(OrderSurplusSplit().also {
                                it.surplus = EntityUtils.toReference(surplus)
                                it.amount = addedAmount
                            })
                            val invoice = SpecificationUtils.getOrLoad(surplus.invoice, context)
                            if (invoice != null) {
                                val pos = invoice.positions.find { it.article == surplus.article }
                                if (pos != null) {
                                    pos.surplusSplits.add(InvoiceSurplusSplit().also {
                                        it.amount = addedAmount
                                        it.order = orderRef
                                    })
                                    invoices.add(invoice)
                                }
                            }
                        }
                    }
                }
                it.amount = amount
            }
            if (order.referenceState.isEmpty()) {
                order.positions.forEach {
                    order.referenceState.add(SerializationProvider.get().clone(it, true))
                }
            }
        }
        return updated
    }
}
