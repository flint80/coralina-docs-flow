/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.userAccount.rest

import com.flinty.docsflow.common.core.model.rest.LogoutRequest
import com.flinty.docsflow.common.core.model.rest.LogoutResponse
import com.flinty.docsflow.server.core.web.DocsFlowAuthFilter
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class LogoutRestHandler:RestHandler<LogoutRequest,LogoutResponse> {
    override fun service(request: LogoutRequest, ctx: RestOperationContext): LogoutResponse {
        ctx.response.setHeader("Set-Cookie", "${DocsFlowAuthFilter.DOCSFLOW_AUTH_COOKIE}=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT")
        return LogoutResponse()
    }
}