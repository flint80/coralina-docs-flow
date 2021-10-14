/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.common

import com.flinty.docsflow.common.core.model.domain.Invoice
import com.flinty.docsflow.common.core.model.domain.InvoicePosition
import com.flinty.docsflow.common.core.model.domain.InvoiceStatus
import com.flinty.docsflow.common.core.model.domain.SpecificationOrder
import com.gridnine.jasmine.common.core.model.EntityUtils
import java.math.BigDecimal

object InvoiceHelper {
    fun createInvoice(order:SpecificationOrder) : Invoice{
        val result = Invoice()
        result.order = EntityUtils.toReference(order)
        result.status = InvoiceStatus.DRAFT
        result.supplier = order.supplier
        order.positions.forEach { pos->
            if(pos.amount > BigDecimal.ZERO){
                result.positions.add(InvoicePosition().also {
                    it.article = pos.article
                    it.name = pos.name
                    it.orderAmount = pos.amount
                    it.unit = pos.unit
                })
            }
        }
        return result
    }
}