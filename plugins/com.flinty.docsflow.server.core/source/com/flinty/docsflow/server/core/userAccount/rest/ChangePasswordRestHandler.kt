/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.server.core.userAccount.rest

import com.flinty.docsflow.common.core.model.domain.UserAccount
import com.flinty.docsflow.common.core.model.rest.ChangePasswordRequest
import com.flinty.docsflow.common.core.model.rest.ChangePasswordResponse
import com.flinty.docsflow.common.core.model.ui.ChangePasswordEditorVV
import com.flinty.docsflow.server.core.web.DocsFlowAuthFilter
import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.utils.DESUtils
import com.gridnine.jasmine.server.core.utils.DigestUtils
import com.gridnine.jasmine.server.standard.helpers.ValidationUtils

class ChangePasswordRestHandler : RestHandler<ChangePasswordRequest, ChangePasswordResponse> {
    override fun service(request: ChangePasswordRequest, ctx: RestOperationContext): ChangePasswordResponse {
        val result = ChangePasswordResponse()
        val validation = ChangePasswordEditorVV()
        if (TextUtils.isBlank(request.vm.newPassword)) {
            validation.newPassword = StandardL10nMessagesFactory.Empty_field()
        }
        if (TextUtils.isBlank(request.vm.retypePassword)) {
            validation.retypePassword = StandardL10nMessagesFactory.Empty_field()
        }
        if (!TextUtils.isBlank(request.vm.newPassword) && !TextUtils.isBlank(request.vm.retypePassword) && request.vm.newPassword != request.vm.retypePassword) {
            validation.retypePassword = "Пароли должны совпадать"
        }
        if (ValidationUtils.hasValidationErrors(validation)) {
            result.vv = validation
            return result
        }
        val userAccount = Storage.get().loadDocument(UserAccount::class, request.userAccountUid, true)
            ?: throw Xeption.forEndUser(L10nMessage("Не удалось загрузить профиль"))
        userAccount.passwordDigest = DigestUtils.getMd5Hash(request.vm.newPassword!!)
        Storage.get().saveDocument(userAccount, true, "изменен пароль")
        if(userAccount.login == AuthUtils.getCurrentUser()){
            ctx.response.setHeader("Set-Cookie", "${DocsFlowAuthFilter.DOCSFLOW_AUTH_COOKIE}=${DESUtils.encrypt("${userAccount.login}|${userAccount.passwordDigest}")};  Path=/")
        }
        return result
    }
}