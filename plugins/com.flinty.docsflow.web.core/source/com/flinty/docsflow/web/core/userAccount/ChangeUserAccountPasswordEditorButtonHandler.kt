/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.web.core.userAccount

import com.flinty.docsflow.common.core.model.domain.UserAccountIndexJS
import com.flinty.docsflow.common.core.model.rest.ChangePasswordRequestJS
import com.flinty.docsflow.common.core.model.ui.ChangePasswordEditor
import com.flinty.docsflow.common.core.model.ui.UserAccountEditorVMJS
import com.flinty.docsflow.common.core.model.ui.UserAccountEditorVSJS
import com.flinty.docsflow.common.core.model.ui.UserAccountEditorVVJS
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.ObjectModificationEvent
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class ChangeUserAccountPasswordEditorButtonHandler :ObjectEditorTool<WebEditor<UserAccountEditorVMJS, UserAccountEditorVSJS, UserAccountEditorVVJS>> {
    override suspend fun invoke(editor: ObjectEditor<WebEditor<UserAccountEditorVMJS, UserAccountEditorVSJS, UserAccountEditorVVJS>>) {
        val passwordEditor = ChangePasswordEditor()
        WebUiLibraryAdapter.get().showDialog(passwordEditor){
            title = "Изменение пароля"
            button {
                displayName = WebMessages.apply
                handler = {
                    val vm = it.getContent().getData()
                    val response = DocsflowRestClient.docsflow_userAccount_changePassword(ChangePasswordRequestJS().also { req ->
                        req.userAccountUid = editor.objectUid
                        req.vm = vm
                    })
                    if(StandardUiUtils.hasValidationErrors(response.vv)){
                        it.getContent().showValidation(response.vv)
                        response.vv
                    } else{
                        it.close()
                        MainFrame.get().publishEvent(ObjectModificationEvent(UserAccountIndexJS.objectId, editor.objectUid))
                        StandardUiUtils.showMessage("Пароль успешно изменен")
                    }
                }
            }
            cancelButton()
        }
    }
}