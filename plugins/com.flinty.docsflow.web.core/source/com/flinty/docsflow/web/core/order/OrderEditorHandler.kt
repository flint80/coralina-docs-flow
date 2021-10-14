/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.web.core.order

import com.flinty.docsflow.common.core.model.domain.SpecificationOrderIndexJS
import com.flinty.docsflow.common.core.model.ui.OrderEditor
import com.flinty.docsflow.web.core.ActionsIds
import com.gridnine.jasmine.web.standard.editor.ObjectEditorHandler
import com.gridnine.jasmine.web.standard.editor.WebEditor

class OrderEditorHandler:ObjectEditorHandler{
    override fun createEditor(): WebEditor<*, *, *> {
        return OrderEditor()
    }

    override fun getActionsGroupId(): String {
        return ActionsIds.com_flinty_docsflow_common_core_model_domain_Order
    }

    override fun getId(): String {
        return SpecificationOrderIndexJS.objectId
    }
}
