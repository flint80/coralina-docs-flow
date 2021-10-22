/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.rest

import com.flinty.docsflow.common.core.model.rest.ParseStandardExcelRequest
import com.flinty.docsflow.common.core.model.rest.ParseStandardExcelResponse
import com.flinty.docsflow.common.core.model.rest.StandardExcelParsedItem
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class ParseStandardExcelRestHandler:RestHandler<ParseStandardExcelRequest, ParseStandardExcelResponse> {
    override fun service(
        request: ParseStandardExcelRequest,
        ctx: RestOperationContext
    ): ParseStandardExcelResponse {
        return ParseStandardExcelResponse().also {resp ->
            request.content.lines().forEach { line ->
                if(!TextUtils.isBlank(line)) {
                    resp.items.add(StandardExcelParsedItem().also { item ->
                        item.fields.addAll(line.split("\t"))
                    })
                }
            }
        }
    }
}