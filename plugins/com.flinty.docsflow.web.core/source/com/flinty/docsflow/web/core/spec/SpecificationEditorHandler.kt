/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.web.core.spec

import com.flinty.docsflow.common.core.model.domain.SpecificationIndexJS
import com.flinty.docsflow.common.core.model.ui.SpecificationEditor
import com.flinty.docsflow.web.core.ActionsIds
import com.gridnine.jasmine.web.standard.editor.ObjectEditorHandler
import com.gridnine.jasmine.web.standard.editor.WebEditor

class SpecificationEditorHandler:ObjectEditorHandler{
    override fun createEditor(): WebEditor<*, *, *> {
        return SpecificationEditor()
    }

    override fun getActionsGroupId(): String {
        return ActionsIds.com_flinty_docsflow_common_core_model_domain_Specification
    }

    override fun getId(): String {
        return SpecificationIndexJS.objectId
    }
}
