/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.storage

import com.flinty.docsflow.common.core.model.domain.Invoice
import com.flinty.docsflow.common.core.model.domain.InvoiceIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler

class InvoiceIndexHandler:IndexHandler<Invoice, InvoiceIndex> {
    override val documentClass = Invoice::class
    override val indexClass = InvoiceIndex::class

    override fun createIndexes(doc: Invoice): List<InvoiceIndex> {
        val index = InvoiceIndex()
        index.uid = doc.uid
        index.invoiceNumber = doc.number
        index.specificationOrder  = doc.order
        index.status = doc.status
        index.supplier = doc.supplier
        return arrayListOf(index)
    }
}