/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.rest

import com.flinty.docsflow.common.core.model.domain.Invoice
import com.flinty.docsflow.common.core.model.domain.InvoiceStatus
import com.flinty.docsflow.common.core.model.rest.CreateWaybillRequest
import com.flinty.docsflow.common.core.model.rest.CreateWaybillResponse
import com.flinty.docsflow.server.core.waybill.storage.WaybillHelper
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class CreateWaybillRestHandler:RestHandler<CreateWaybillRequest, CreateWaybillResponse> {
    override fun service(request: CreateWaybillRequest, ctx: RestOperationContext): CreateWaybillResponse {
        val invoice = Storage.get().loadDocument(Invoice::class, request.invoiceUid)!!
        if(invoice.waybills.isNotEmpty()){
            return CreateWaybillResponse().also {
                it.waybillUid = invoice.waybills[0].uid
            }
        }
        invoice.status = InvoiceStatus.FIXED
        val waybill = WaybillHelper.createWaybill(invoice)
        Storage.get().saveDocument(waybill, skipInterceptors = true )
        invoice.waybills.add(EntityUtils.toReference(waybill))
        Storage.get().saveDocument(invoice, skipInterceptors = true)
        return CreateWaybillResponse().also {
            it.waybillUid = waybill.uid
        }
    }

}