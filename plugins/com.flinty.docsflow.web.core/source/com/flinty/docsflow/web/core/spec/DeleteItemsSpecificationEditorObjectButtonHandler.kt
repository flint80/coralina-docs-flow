/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.spec

import com.flinty.docsflow.common.core.model.ui.SpecificationEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool

class DeleteItemsSpecificationEditorObjectButtonHandler: ObjectEditorTool<SpecificationEditor> {
    override suspend fun invoke(editor: ObjectEditor<SpecificationEditor>) {
        editor.getEditor().specificationWidget.deleteSelectedItems()
    }
}