/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.server.core.spec.common.SpecificationUtils
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.server.core.storage.OperationContext
import com.gridnine.jasmine.server.core.storage.StorageInterceptor
import com.gridnine.jasmine.server.standard.model.SequenceNumberGenerator

class SpecificationInterceptor : StorageInterceptor {
    override val priority = 1.0
    override fun <D : BaseDocument> onSave(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is Specification) {
            val newSpecification = doc as Specification
            val specRef = EntityUtils.toReference(newSpecification)
            val oldSpecification = context.globalContext.oldObject as Specification?
            val ordersToCorrect = oldSpecification?.positions?.mapNotNull { it.order }?.distinct() ?: arrayListOf()
            val newItems = ArrayList(newSpecification.positions)
            ordersToCorrect.forEach { orderRef ->
                val existingOrder = SpecificationUtils.getOrLoad(orderRef, context)!!
                Storage.get().loadDocument(orderRef, true)!!
                val updated = updateOrder(existingOrder, newItems, specRef, existingOrder.status != OrderStatus.DRAFT)

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
                    SpecificationUtils.getOrLoad(Storage.get().searchDocuments(SpecificationOrderIndex::class, searchQuery {
                        where {
                            eq(SpecificationOrderIndex.statusProperty, OrderStatus.DRAFT)
                            eq(SpecificationOrderIndex.supplierProperty, supp)
                        }
                    }).firstOrNull()?.document, context) ?: SpecificationOrder().also {
                        it.status = OrderStatus.DRAFT
                        it.code = "O-${SequenceNumberGenerator.get().incrementAndGet("O")}"
                        it.supplier = supp
                    }
                updateOrder(appropriateOrder, newItems, specRef, appropriateOrder.status != OrderStatus.DRAFT)
                Storage.get().saveDocument(appropriateOrder)
            }
            if(newSpecification.positions.any {
                val res = it.orderAmount?.compareTo(it.toBeOrdered) != 0
                    res
            }){
                newSpecification.status = SpecificationStatus.PARTIALLY_ORDERED
            } else {
                newSpecification.status = SpecificationStatus.ORDERED
            }
        }
    }



    override fun <D : BaseDocument> onDelete(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is Specification ) {
            val specification = doc as Specification
            val specRef = EntityUtils.toReference(specification)
            val ordersToCorrect = specification.positions.mapNotNull { it.order }.distinct()
            ordersToCorrect.forEach { orderRef ->
                val existingOrder = SpecificationUtils.getOrLoad(orderRef, context)!!
                val updated = updateOrder(existingOrder, arrayListOf(), specRef, existingOrder.status != OrderStatus.DRAFT)
                if (existingOrder.status != OrderStatus.DRAFT && updated) {
                    throw Xeption.forEndUser(L10nMessage("спецификация приводит к изменению заказа ${existingOrder.code}"))
                }
                if (updated) {
                    if (existingOrder.positions.isEmpty()) {
                        Storage.get().deleteDocument(existingOrder)
                    } else {
                        Storage.get().saveDocument(existingOrder)
                    }
                }
            }
        }
    }
    private fun updateOrder(order: SpecificationOrder, items: ArrayList<SpecificationPosition>, specRef:ObjectReference<Specification>, prohibitModification:Boolean): Boolean {

        var updated = false
        val positionsToDelete = arrayListOf<OrderPosition>()
        val processedItems = arrayListOf<SpecificationPosition>()
        val orderRef = EntityUtils.toReference(order)
        order.positions.forEach { pos ->
            val splitsToDelete = arrayListOf<OrderSpecificationSplit>()
            pos.specificationSplits.forEach {oss ->
                if(oss.specification == specRef){
                    val newItem = items.find { it.supplier == order.supplier && it.article == pos.article }
                    if(newItem == null){
                        splitsToDelete.add(oss)
                        updated = true
                    } else {
                        if(newItem.unit != pos.unit){
                            throw Xeption.forEndUser(L10nMessage("позиция ${newItem.article} в спецификации имеет единицу измерения " +
                                    "${newItem.unit} отличную от единицы имерения в заказе $order"))
                        }
                        processedItems.add(newItem)
                        if(oss.amount.compareTo(newItem.toBeOrdered) != 0){
                            oss.amount = newItem.toBeOrdered
                            newItem.order = orderRef
                            newItem.orderAmount = newItem.toBeOrdered
                            if(prohibitModification){
                                throw Xeption.forEndUser(L10nMessage("позиция ${newItem.article} спецификации ведет к изменению заказа $order"))
                            }
                            updated = true
                        }
                    }
                }
            }
            pos.specificationSplits.removeAll(splitsToDelete)
            if(pos.specificationSplits.isEmpty()){
                positionsToDelete.add(pos)
                if(prohibitModification){
                    throw Xeption.forEndUser(L10nMessage("из заказа $order будет удалена позиция ${pos.article}"))
                }
                updated = true
            }
        }
        order.positions.removeAll(positionsToDelete)
        items.removeAll(processedItems)
        processedItems.clear()
        items.filter { it.supplier == order.supplier }.forEach{item->
            processedItems.add(item)
            val position = order.positions.find { item.article == it.article }?:run{
                OrderPosition().also {
                    it.article = item.article
                    it.name = item.name
                    it.unit = item.unit
                    it.amount = it.specificationSplits.sumOf { it.amount }.add(item.toBeOrdered)
                    order.positions.add(it)
                }
            }
            position.specificationSplits.add(OrderSpecificationSplit().also {
                it.amount = item.toBeOrdered
                it.specification = specRef
                item.orderAmount = it.amount
                item.order = orderRef
            })
            if(prohibitModification){
                throw Xeption.forEndUser(L10nMessage("позиция ${item.article} спецификации ведет к изменению заказа $order"))
            }
            updated = true
        }
        items.removeAll(processedItems)
        if(updated){
            order.status = OrderStatus.DRAFT
            if(order.referenceState.isEmpty()){
                order.positions.forEach {
                    order.referenceState.add(SerializationProvider.get().clone(it, true))
                }
            }
            order.positions.forEach {
                it.amount = it.specificationSplits.sumOf { it.amount }
            }
        }
        return updated
    }
}
