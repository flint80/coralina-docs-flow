/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.userAccount.rest

import com.flinty.docsflow.common.core.model.domain.UserAccountIndex
import com.flinty.docsflow.common.core.model.rest.LoginRequest
import com.flinty.docsflow.common.core.model.rest.LoginResponse
import com.flinty.docsflow.server.core.web.DocsFlowAuthFilter
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.utils.DESUtils
import com.gridnine.jasmine.server.core.utils.DigestUtils

class LoginRestHandler:RestHandler<LoginRequest,LoginResponse> {
    override fun service(request: LoginRequest, ctx: RestOperationContext): LoginResponse {
        val response = LoginResponse()
        response.success = true
        if(TextUtils.isBlank(request.login)){
            response.success = false
            response.loginErrorMessage = StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(request.password)){
            response.success = false
            response.passwordErrorMessage = StandardL10nMessagesFactory.Empty_field()
        }
        if(!response.success){
            return response
        }
        val userAccount = Storage.get().findUniqueDocument(UserAccountIndex::class, UserAccountIndex.loginProperty, request.login)
        if(userAccount == null){
            response.success = false
            response.loginErrorMessage = "Пользователя с таким логином не существует"
            return response
        }
        val digest = DigestUtils.getMd5Hash(request.password!!)
        if(userAccount.passwordDigest != digest){
            response.success = false
            response.passwordErrorMessage = "Неправильный пароль"
            return response
        }
        response.success = true
        ctx.response.setHeader("Set-Cookie", "${DocsFlowAuthFilter.DOCSFLOW_AUTH_COOKIE}=${DESUtils.encrypt("${userAccount.login}|${digest}")};  Path=/")
        return response
    }
}