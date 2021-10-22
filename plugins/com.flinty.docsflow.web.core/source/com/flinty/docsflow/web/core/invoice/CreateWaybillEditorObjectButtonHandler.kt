/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.invoice

import com.flinty.docsflow.common.core.model.domain.InvoiceIndexJS
import com.flinty.docsflow.common.core.model.domain.WaybillIndexJS
import com.flinty.docsflow.common.core.model.rest.CreateWaybillRequestJS
import com.flinty.docsflow.common.core.model.ui.InvoiceEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.ObjectModificationEvent

class CreateWaybillEditorObjectButtonHandler : ObjectEditorTool<InvoiceEditor> {
    override suspend fun invoke(editor: ObjectEditor<InvoiceEditor>) {
        val response = DocsflowRestClient.docsflow_invoice_createWaybill(CreateWaybillRequestJS().also {
            it.invoiceUid = editor.objectUid
        })
        MainFrame.get().publishEvent(ObjectModificationEvent(InvoiceIndexJS.objectId, editor.objectUid))
        MainFrame.get().openTab(OpenObjectData(WaybillIndexJS.objectId, response.waybillUid, null))
    }
}