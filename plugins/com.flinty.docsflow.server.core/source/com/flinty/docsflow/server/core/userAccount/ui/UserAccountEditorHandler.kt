/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.userAccount.ui

import com.flinty.docsflow.common.core.model.domain.UserAccount
import com.flinty.docsflow.common.core.model.ui.UserAccountEditorVM
import com.flinty.docsflow.common.core.model.ui.UserAccountEditorVS
import com.flinty.docsflow.common.core.model.ui.UserAccountEditorVV
import com.gridnine.jasmine.common.core.model.TextBoxConfiguration
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import kotlin.reflect.KClass

class UserAccountEditorHandler:ObjectEditorHandler<UserAccount, UserAccountEditorVM, UserAccountEditorVS, UserAccountEditorVV>{
    override fun getObjectClass(): KClass<UserAccount> {
        return UserAccount::class
    }

    override fun getVMClass(): KClass<UserAccountEditorVM> {
        return UserAccountEditorVM::class
    }

    override fun getVSClass(): KClass<UserAccountEditorVS> {
        return UserAccountEditorVS::class
    }

    override fun getVVClass(): KClass<UserAccountEditorVV> {
        return UserAccountEditorVV::class
    }

    override fun fillSettings(entity: UserAccount, vsEntity: UserAccountEditorVS, vmEntity: UserAccountEditorVM, ctx: MutableMap<String, Any?>) {
        vsEntity.login = TextBoxConfiguration{notEditable = true}
    }
    override fun read(entity: UserAccount, vmEntity: UserAccountEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.login  = entity.login
        vmEntity.name = entity.name
    }

    override fun getTitle(entity: UserAccount, vmEntity: UserAccountEditorVM, vsEntity: UserAccountEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.name
    }

    override fun write(entity: UserAccount, vmEntity: UserAccountEditorVM, ctx: MutableMap<String, Any?>) {
        entity.login = vmEntity.login
        entity.name = vmEntity.name
    }

    override fun validate(vmEntity: UserAccountEditorVM, vvEntity: UserAccountEditorVV, ctx: MutableMap<String, Any?>) {
        if(TextUtils.isBlank(vmEntity.login)){
            vvEntity.login = StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(vmEntity.name)){
            vvEntity.name = StandardL10nMessagesFactory.Empty_field()
        }
    }
}