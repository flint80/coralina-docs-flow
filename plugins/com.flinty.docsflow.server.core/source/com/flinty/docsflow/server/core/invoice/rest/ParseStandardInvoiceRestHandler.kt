/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.rest

import com.flinty.docsflow.common.core.model.rest.ParseStandardInvoiceRequest
import com.flinty.docsflow.common.core.model.rest.ParseStandardInvoiceResponse
import com.flinty.docsflow.common.core.model.rest.StandardInvoiceParsedItem
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class ParseStandardInvoiceRestHandler:RestHandler<ParseStandardInvoiceRequest, ParseStandardInvoiceResponse> {
    override fun service(
        request: ParseStandardInvoiceRequest,
        ctx: RestOperationContext
    ): ParseStandardInvoiceResponse {
        return ParseStandardInvoiceResponse().also {resp ->
            request.content.lines().forEach { line ->
                resp.items.add(StandardInvoiceParsedItem().also {item ->
                    item.fields.addAll(line.split("\t"))
                })
            }
        }
    }
}