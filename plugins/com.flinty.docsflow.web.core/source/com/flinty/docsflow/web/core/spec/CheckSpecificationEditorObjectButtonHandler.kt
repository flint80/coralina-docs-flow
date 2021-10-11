/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.spec

import com.flinty.docsflow.common.core.model.ui.SpecificationEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class CheckSpecificationEditorObjectButtonHandler:
    ObjectEditorTool<SpecificationEditor> {
    override suspend fun invoke(editor: ObjectEditor<SpecificationEditor>) {
        val items = editor.getEditor().specificationWidget.getData().items
        StandardUiUtils.showMessage("${items.sumOf { it.toBeOrdered!! }}(${items.size} позиций)")
    }
}