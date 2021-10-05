/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.web.core.userAccount

import com.flinty.docsflow.common.core.model.domain.UserAccountIndexJS
import com.flinty.docsflow.common.core.model.ui.UserAccountEditor
import com.flinty.docsflow.web.core.ActionsIds
import com.gridnine.jasmine.web.standard.editor.ObjectEditorHandler
import com.gridnine.jasmine.web.standard.editor.WebEditor

class UserAccountEditorHandler:ObjectEditorHandler{
    override fun createEditor(): WebEditor<*, *, *> {
        return UserAccountEditor()
    }

    override fun getActionsGroupId(): String {
        return ActionsIds.com_flinty_docsflow_common_core_model_domain_UserAccount
    }

    override fun getId(): String {
        return UserAccountIndexJS.objectId
    }
}
