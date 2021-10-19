/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.server.core.spec.common.SpecificationUtils
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.storage.SimpleCriterion
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.server.core.storage.OperationContext
import com.gridnine.jasmine.server.core.storage.StorageInterceptor
import java.math.BigDecimal

class InvoiceInterceptor : StorageInterceptor {
    override val priority = 1.0
    override fun <D : BaseDocument> onSave(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is Invoice) {
            val oldInvoice = context.globalContext.oldObject as Invoice?
            if (oldInvoice?.status == InvoiceStatus.FIXED && doc.status != InvoiceStatus.FIXED) {
                deleteSurpluses(doc, context)
            } else if (oldInvoice?.status != InvoiceStatus.FIXED && doc.status == InvoiceStatus.FIXED) {
                val modifiedOrders = hashSetOf<SpecificationOrder>()
                val currentOrder = SpecificationUtils.getOrLoad(doc.order, context)!!
                currentOrder.status = OrderStatus.FIXED
                modifiedOrders.add(currentOrder)
                doc.positions.forEach { ip ->
                    if (ip.invoiceAmount != null && ip.invoiceAmount!! > ip.orderAmount) {
                        val surplus = Surplus().also {
                            it.status = SurplusStatus.DRAFT
                            it.invoice = EntityUtils.toReference(doc)
                            it.article = ip.article
                            it.name = ip.name
                            it.unit = ip.unit
                            it.order = doc.order
                        }
                        surplus.amount = ip.invoiceAmount!!.subtract(ip.orderAmount)
                        val surplusRef = EntityUtils.toReference(surplus)
                        ip.surplus = surplusRef
                        val order = SpecificationUtils.getOrLoad(
                            Storage.get().searchDocuments(SpecificationOrderIndex::class, searchQuery {
                                where {
                                    eq(SpecificationOrderIndex.statusProperty, OrderStatus.DRAFT)
                                    eq(SpecificationOrderIndex.supplierProperty, doc.supplier)
                                    ne(BaseIndex.documentField, doc.order)
                                }
                            }).firstOrNull()?.document, context
                        )
                        if (order != null) {
                            val pos = order.positions.find { it.article == ip.article }
                            if (pos != null) {
                                val remains = surplus.amount.subtract(surplus.splits.sumOf { it.amount })
                                if (remains > BigDecimal.ZERO) {
                                    pos.surplusSplits.add(OrderSurplusSplit().also {
                                        it.amount = if (remains > pos.amount) pos.amount else remains
                                        it.surplus = surplusRef
                                    })
                                    modifiedOrders.add(order)
                                    surplus.splits.add(SurplusSplit().also {
                                        it.amount = if (remains > pos.amount) pos.amount else remains
                                        it.order = EntityUtils.toReference(order)
                                    })
                                    ip.surplusSplits.add(InvoiceSurplusSplit().also {
                                        it.amount = if (remains > pos.amount) pos.amount else remains
                                        it.order = EntityUtils.toReference(order)
                                    })
                                }
                            }
                        }
                        Storage.get().saveDocument(surplus)
                    }
                }
                modifiedOrders.forEach {
                    Storage.get().saveDocument(it)
                }
            }
        }
    }

    override fun <D : BaseDocument> onDelete(doc: D, context: OperationContext<D>) {
        if (doc is Invoice) {
            if (doc.status == InvoiceStatus.FIXED) {
                deleteSurpluses(doc, context)
            }
            val order = SpecificationUtils.getOrLoad(doc.order, context)!!
            order.invoice = null
            Storage.get().saveDocument(order)
        }
    }

    private fun <D : BaseDocument> deleteSurpluses(invoice: Invoice, context: OperationContext<D>) {
        val surplusesToDelete = arrayListOf<Surplus>()
        invoice.positions.forEach {
            val surplus = SpecificationUtils.getOrLoad(it.surplus, context)
            if (surplus != null && !surplusesToDelete.contains(surplus)) {
                surplusesToDelete.add(surplus)
            }
            it.surplus = null
            it.surplusSplits.clear()
        }
        if (surplusesToDelete.isEmpty()) {
            return
        }
        val modifiedOrders = hashSetOf<SpecificationOrder>()
        surplusesToDelete.forEach { surplus ->
            if (surplus.status == SurplusStatus.FIXED) {
                throw Xeption.forEndUser(L10nMessage("изменение счета ${invoice.number} ведет к удалению зафиксированного излишка по позиции ${surplus.article}"))
            }
            val orders = surplus.splits.mapNotNull { SpecificationUtils.getOrLoad(it.order, context) }.distinct()

            surplus.splits.forEach { sp ->
                val order = orders.find { sp.order.uid == it.uid }!!
                val position = order.positions.find { it.article == surplus.article }
                if (position != null) {
                    val deleted = position.surplusSplits.removeIf { it.surplus.uid == surplus.uid }
                    if (deleted) {
                        if (order.status == OrderStatus.FIXED) {
                            throw Xeption.forEndUser(L10nMessage("изменение счета ${invoice.number} ведет к удалению зафиксированного заказа $order по позиции ${surplus.article}"))
                        }
                        modifiedOrders.add(order)
                    }
                }
            }
            Storage.get().deleteDocument(surplus)
        }
        modifiedOrders.forEach { Storage.get().saveDocument(it) }
    }


}
