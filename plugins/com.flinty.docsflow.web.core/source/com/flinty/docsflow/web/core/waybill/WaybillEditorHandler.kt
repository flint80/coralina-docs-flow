/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.web.core.waybill

import com.flinty.docsflow.common.core.model.domain.WaybillIndexJS
import com.flinty.docsflow.common.core.model.ui.WaybillEditor
import com.flinty.docsflow.web.core.ActionsIds
import com.gridnine.jasmine.web.standard.editor.ObjectEditorHandler
import com.gridnine.jasmine.web.standard.editor.WebEditor

class WaybillEditorHandler:ObjectEditorHandler{
    override fun createEditor(): WebEditor<*, *, *> {
        return WaybillEditor()
    }

    override fun getActionsGroupId(): String {
        return ActionsIds.com_flinty_docsflow_common_core_model_domain_Waybill
    }

    override fun getId(): String {
        return WaybillIndexJS.objectId
    }
}
