/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.order.rest

import com.flinty.docsflow.common.core.model.domain.SpecificationOrder
import com.flinty.docsflow.common.core.model.rest.CreateInvoiceRestRequest
import com.flinty.docsflow.common.core.model.rest.CreateInvoiceRestResponse
import com.flinty.docsflow.common.core.model.rest.DownloadOrderRequest
import com.flinty.docsflow.common.core.model.rest.DownloadOrderResponse
import com.flinty.docsflow.server.core.invoice.common.InvoiceHelper
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellBorderWidth
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellHorizontalAlignment
import com.gridnine.jasmine.common.standard.rest.FileDataDT
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.reports.builders.report
import com.gridnine.jasmine.server.reports.excel.ExcelGenerator
import java.math.BigDecimal
import java.util.*

class CreateInvoiceRestHandler:RestHandler<CreateInvoiceRestRequest, CreateInvoiceRestResponse> {
    override fun service(request: CreateInvoiceRestRequest, ctx: RestOperationContext): CreateInvoiceRestResponse {
        val order = Storage.get().loadDocument(SpecificationOrder::class, request.orderUid)!!
        if(order.invoice != null){
            throw Xeption.forEndUser(L10nMessage("для этого заказа уже создан счет ${order.invoice}"))
        }
        val invoice = InvoiceHelper.createInvoice(order)
        Storage.get().saveDocument(invoice)
        order.invoice = EntityUtils.toReference(invoice)
        Storage.get().saveDocument(order)
        return CreateInvoiceRestResponse().also {
            it.invoiceUid = invoice.uid
        }
    }
}