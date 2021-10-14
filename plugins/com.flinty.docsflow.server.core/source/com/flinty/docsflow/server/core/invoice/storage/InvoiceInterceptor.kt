/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.server.core.spec.common.SpecificationUtils
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.OperationContext
import com.gridnine.jasmine.server.core.storage.StorageInterceptor

class InvoiceInterceptor : StorageInterceptor {
    override val priority = 1.0
    override fun <D : BaseDocument> onSave(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is Invoice) {
            val oldInvoice = context.globalContext.oldObject as Invoice?
            if(oldInvoice?.status == InvoiceStatus.FIXED && doc.status != InvoiceStatus.FIXED){
                deleteSurpluses(oldInvoice, context)
            } else if(oldInvoice?.status != InvoiceStatus.FIXED && doc.status == InvoiceStatus.FIXED){
                doc.positions.forEach {ip->
                    if(ip.invoiceAmount != null && ip.invoiceAmount!! > ip.orderAmount){
                        val surplus = SpecificationUtils.getOrLoad(ip.surplus, context)?:Surplus().also {
                            it.status = SurplusStatus.DRAFT
                            it.invoice = EntityUtils.toReference(doc)
                            it.article = ip.article
                            it.name = ip.name
                            it.unit = ip.unit
                        }
                        surplus.amount = ip.invoiceAmount!!.subtract(ip.orderAmount)
                        Storage.get().saveDocument(surplus)
                    }
                }
            }
        }
    }
    override fun <D : BaseDocument> onDelete(doc: D, context: OperationContext<D>) {
        if (doc is Invoice) {
                if(doc.status == InvoiceStatus.FIXED){
                    deleteSurpluses(doc, context)
            }
        }
    }

    private fun<D : BaseDocument>  deleteSurpluses(invoice: Invoice, context: OperationContext<D>) {
        val surplusesToDelete = arrayListOf<Surplus>()
        invoice.positions.forEach {
            val surplus = SpecificationUtils.getOrLoad(it.surplus, context)
            if(surplus != null && !surplusesToDelete.contains(surplus)){
                surplusesToDelete.add(surplus)
            }
        }
        if(surplusesToDelete.isEmpty()){
            return
        }
        surplusesToDelete.forEach {
            if(it.status == SurplusStatus.FIXED){
                throw Xeption.forEndUser(L10nMessage("изменение счета ${invoice.number} ведет к удалению зафиксированного излишка по позиции ${it.article}"))
            }
            Storage.get().deleteDocument(it)
        }
    }


}
