/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.waybill.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.gridnine.jasmine.common.core.model.EntityUtils
import java.math.BigDecimal

object WaybillHelper {
    fun createWaybill(invoice: Invoice): Waybill {
        val result = Waybill()
        result.invoice = EntityUtils.toReference(invoice)
        result.status = WaybillStatus.DRAFT
        result.supplier = invoice.supplier
        invoice.positions.forEach { pos ->
            result.positions.add(WaybillPosition().also {
                it.article = pos.article
                it.name = pos.name
                it.invoiceAmount = pos.invoiceAmount!!
                it.unit = pos.unit
                it.amount = BigDecimal.ZERO
            })
        }
        return result
    }
}