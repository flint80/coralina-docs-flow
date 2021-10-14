/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.order

import com.flinty.docsflow.common.core.model.rest.CalculateOrderDifferenceRequestJS
import com.flinty.docsflow.common.core.model.ui.OrderEditor
import com.flinty.docsflow.common.core.model.ui.ShowOrderDifferenceDialog
import com.flinty.docsflow.common.core.model.ui.SpecificationEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class ShowOrderDifferenceEditorObjectButtonHandler:
    ObjectEditorTool<OrderEditor> {
    override suspend fun invoke(editor: ObjectEditor<OrderEditor>) {
        val result = DocsflowRestClient.docsflow_order_calculateDifference(CalculateOrderDifferenceRequestJS().also {
            it.orderUid = editor.objectUid
        })
        if(result.vm.positions.isEmpty()){
            StandardUiUtils.showMessage("Различий не найдено")
            return
        }
        val dialog = ShowOrderDifferenceDialog()
        dialog.positionsWidget.readData(result.vm.positions, null)
        WebUiLibraryAdapter.get().showDialog(dialog){
            title = "Различия"
            cancelButton()
        }
    }
}