/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.order

import com.flinty.docsflow.common.core.model.domain.InvoiceIndexJS
import com.flinty.docsflow.common.core.model.rest.CreateInvoiceRestRequestJS
import com.flinty.docsflow.common.core.model.rest.DownloadOrderRequestJS
import com.flinty.docsflow.common.core.model.ui.OrderEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.core.utils.ContentTypeJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.mainframe.MainFrame

class CreateInvoiceOrderEditorObjectButtonHandler:
    ObjectEditorTool<OrderEditor> {
    override suspend fun invoke(editor: ObjectEditor<OrderEditor>) {
        val result = DocsflowRestClient.docsflow_order_createInvoice(CreateInvoiceRestRequestJS().also {
            it.orderUid = editor.objectUid
        })
        MainFrame.get().openTab(OpenObjectData(InvoiceIndexJS.objectId, result.invoiceUid, null, true))
    }
}