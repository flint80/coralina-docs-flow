/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.order

import com.flinty.docsflow.common.core.model.domain.SpecificationOrderIndexJS
import com.flinty.docsflow.common.core.model.rest.ReplaceOrderPositionRestRequestJS
import com.flinty.docsflow.common.core.model.ui.OrderEditor
import com.flinty.docsflow.common.core.model.ui.ReplaceOrderPositionDialog
import com.flinty.docsflow.common.core.model.ui.ReplaceOrderPositionDialogVMJS
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.ObjectModificationEvent
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class ReplaceOrderPositionEditorObjectButtonHandler :  ObjectEditorTool<OrderEditor> {
    override suspend fun invoke(editor: ObjectEditor<OrderEditor>) {
        val selected = editor.getEditor().positionsWidget.getSelectedValue() ?: return
        val editorDialog = ReplaceOrderPositionDialog()
        editorDialog.readData(ReplaceOrderPositionDialogVMJS().also {
            it.oldArticle = selected.article
        }, null)
        WebUiLibraryAdapter.get().showDialog(editorDialog){
            title = "Замена позиции"
            button {
                displayName = WebMessages.apply
                handler ={
                    val data = it.getContent().getData()
                    val response = DocsflowRestClient.docsflow_order_replacePosition(ReplaceOrderPositionRestRequestJS().also {
                        it.orderUid = editor.objectUid
                        it.newArticle = data.newArticle
                        it.oldArticle = selected.article!!
                        it.newName = data.newName
                    })
                    if(StandardUiUtils.hasValidationErrors(response.vv)){
                        it.getContent().showValidation(response.vv)
                    } else {
                        it.close()
                        MainFrame.get().publishEvent(ObjectModificationEvent(SpecificationOrderIndexJS.objectId, editor.objectUid))
                        response.modifiedSpecifications.forEach { ref ->
                            MainFrame.get().publishEvent(ObjectModificationEvent(ref.type, ref.uid))
                        }
                    }
                    Unit
                }
            }
            cancelButton()
        }
    }
}