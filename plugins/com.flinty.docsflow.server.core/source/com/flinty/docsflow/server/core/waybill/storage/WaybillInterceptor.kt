/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.waybill.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.server.core.spec.common.SpecificationUtils
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.server.core.storage.OperationContext
import com.gridnine.jasmine.server.core.storage.StorageInterceptor
import java.math.BigDecimal

class WaybillInterceptor : StorageInterceptor {
    override val priority = 1.0
    override fun <D : BaseDocument> onSave(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is Waybill) {
            val oldWaybill = context.globalContext.oldObject as Waybill?
            if (oldWaybill?.status == WaybillStatus.FIXED && doc.status != WaybillStatus.FIXED) {
                unholdEntities(oldWaybill, context)
            } else if (oldWaybill?.status != WaybillStatus.FIXED && doc.status == WaybillStatus.FIXED) {
                val waybillRef = EntityUtils.toReference(doc) as ObjectReference<Waybill>
                val relatedWaybills = Storage.get().searchDocuments(WaybillIndex::class, searchQuery {
                    where {
                        eq(WaybillIndex.invoiceProperty, doc.invoice)
                        ne(BaseIndex.documentField, waybillRef)
                    }
                }).map { SpecificationUtils.getOrLoad(it.document, context)!! }
                val invoice = SpecificationUtils.getOrLoad(doc.invoice, context)!!
                invoice.status = InvoiceStatus.FIXED
                invoice.waybills.remove(waybillRef)
                invoice.waybills.add(waybillRef)
                Storage.get().saveDocument(invoice, skipInterceptors = true)
                doc.positions.forEach { wbPos ->
                    val invoicePos = invoice.positions.find { it.article == wbPos.article }!!
                    val surplus = SpecificationUtils.getOrLoad(invoicePos.surplus, context)
                    if (surplus != null) {
                        surplus.status = SurplusStatus.FIXED
                        Storage.get().saveDocument(surplus, skipInterceptors = true)
                    }
                    var remains = wbPos.amount
                    invoicePos.specificationSplits.forEach { specSplit ->
                        var amount = if (remains > specSplit.amount) specSplit.amount else remains
                        amount -= relatedWaybills.sumOf {
                            val otherPos = it.positions.find { it.article == wbPos.article }
                            otherPos?.specificationsSplit?.filter { it.specification == specSplit.specification }
                                ?.sumOf { it.amount } ?: BigDecimal.ZERO
                        }
                        if (amount > BigDecimal.ZERO) {
                            wbPos.specificationsSplit.add(WaybillSpecificationSplit().also {
                                it.amount = amount
                                it.specification = specSplit.specification
                            })
                        }
                        remains -= amount
                    }

                    wbPos.storeAmount = wbPos.amount.subtract(wbPos.specificationsSplit.sumOf { it.amount })
                }
            }
        }
    }


    private fun <D : BaseDocument> unholdEntities(waybill: Waybill, context: OperationContext<D>) {
        val relatedWaybills = Storage.get().searchDocuments(WaybillIndex::class, searchQuery {
            where {
                eq(WaybillIndex.invoiceProperty, waybill.invoice)
                ne(BaseIndex.documentField, EntityUtils.toReference(waybill))
            }
        }).map { SpecificationUtils.getOrLoad(it.document, context)!! }
        val invoice = SpecificationUtils.getOrLoad(waybill.invoice, context)!!
        invoice.waybills.remove(EntityUtils.toReference(waybill))
        if (!relatedWaybills.any { it.status == WaybillStatus.FIXED }) {
            invoice.status = InvoiceStatus.DRAFT
            Storage.get().saveDocument(invoice)
        }
        invoice.positions.forEach { pos ->
            val surplus = SpecificationUtils.getOrLoad(pos.surplus, context)
            if (surplus != null) {
                if (waybill.positions.any { it.article == surplus.article }) {
                    val stillHold =
                        relatedWaybills.any { it.status == WaybillStatus.FIXED && it.positions.any { it.article == surplus.article } }
                    if (!stillHold) {
                        surplus.status = SurplusStatus.DRAFT
                        Storage.get().saveDocument(surplus)
                    }
                }
            }
        }
        waybill.positions.forEach { pos ->
            pos.specificationsSplit.clear()
            pos.storeAmount = pos.amount
        }


    }

    override fun <D : BaseDocument> onDelete(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is Waybill) {
            unholdEntities(doc, context)
        }
    }

}
