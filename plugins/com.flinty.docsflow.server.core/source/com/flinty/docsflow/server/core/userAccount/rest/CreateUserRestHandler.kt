/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.userAccount.rest

import com.flinty.docsflow.common.core.model.domain.UserAccount
import com.flinty.docsflow.common.core.model.domain.UserAccountIndex
import com.flinty.docsflow.common.core.model.rest.CreateUserAccountRequest
import com.flinty.docsflow.common.core.model.rest.CreateUserAccountResponse
import com.flinty.docsflow.common.core.model.ui.NewUserAccountEditorVV
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.utils.DigestUtils
import com.gridnine.jasmine.server.standard.helpers.ValidationUtils

class CreateUserRestHandler :RestHandler<CreateUserAccountRequest,CreateUserAccountResponse>{
    override fun service(request: CreateUserAccountRequest, ctx: RestOperationContext): CreateUserAccountResponse {
        val result = CreateUserAccountResponse()
        val validation = NewUserAccountEditorVV()
        if(TextUtils.isBlank(request.vm.login)){
            validation.login = StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(request.vm.password)){
            validation.password =  StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(request.vm.retypePassword)){
            validation.retypePassword =  StandardL10nMessagesFactory.Empty_field()
        }
        if(!TextUtils.isBlank(request.vm.login)){
            if(Storage.get().findUniqueDocumentReference(UserAccountIndex::class, UserAccountIndex.loginProperty, request.vm.login) != null){
                validation.login = "Логин уже существует"
            }
        }
        if(!TextUtils.isBlank(request.vm.password) && !TextUtils.isBlank(request.vm.retypePassword) && request.vm.password != request.vm.retypePassword){
            validation.retypePassword = "Пароли должны совпадать"
        }
        if(ValidationUtils.hasValidationErrors(validation)){
            result.vv = validation
            return result
        }
        val account = UserAccount()
        account.login = request.vm.login
        account.passwordDigest = DigestUtils.getMd5Hash(request.vm.password!!)
        account.name = request.vm.name
        Storage.get().saveDocument(account)
        result.objectUid = account.uid
        return result
    }
}