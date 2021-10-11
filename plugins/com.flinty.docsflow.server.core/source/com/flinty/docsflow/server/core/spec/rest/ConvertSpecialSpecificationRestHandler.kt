/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.rest

import com.flinty.docsflow.common.core.model.rest.ConvertSpecialSpecificationRequest
import com.flinty.docsflow.common.core.model.rest.ConvertSpecialSpecificationResponse
import com.flinty.docsflow.server.core.spec.parser.SpecialSpecificationParser
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import java.util.*

class ConvertSpecialSpecificationRestHandler:RestHandler<ConvertSpecialSpecificationRequest, ConvertSpecialSpecificationResponse> {
    override fun service(
        request: ConvertSpecialSpecificationRequest,
        ctx: RestOperationContext
    ): ConvertSpecialSpecificationResponse {
        return ConvertSpecialSpecificationResponse().also {
            it.content = Base64.getEncoder().encodeToString(SpecialSpecificationParser.convert(Base64.getDecoder().decode(request.content)))
        }
    }

}