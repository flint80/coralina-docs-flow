/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.web.core.surplus

import com.flinty.docsflow.common.core.model.domain.SurplusIndexJS
import com.flinty.docsflow.common.core.model.ui.SurplusEditor
import com.flinty.docsflow.web.core.ActionsIds
import com.gridnine.jasmine.web.standard.editor.ObjectEditorHandler
import com.gridnine.jasmine.web.standard.editor.WebEditor

class SurplusEditorHandler:ObjectEditorHandler{
    override fun createEditor(): WebEditor<*, *, *> {
        return SurplusEditor()
    }

    override fun getActionsGroupId(): String {
        return ActionsIds.com_flinty_docsflow_common_core_model_domain_Surplus
    }

    override fun getId(): String {
        return SurplusIndexJS.objectId
    }
}
