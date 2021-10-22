/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.invoice

import com.flinty.docsflow.common.core.model.rest.ParseSchneiderInvoiceRequestJS
import com.flinty.docsflow.common.core.model.ui.InvoiceEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import kotlinx.browser.window

class ImportSchneiderInvoiceEditorObjectButtonHandler:
    ObjectEditorTool<InvoiceEditor> {
    override suspend fun invoke(editor: ObjectEditor<InvoiceEditor>) {
        window.navigator.clipboard.readText().then { value ->
               launch {
                   val result = DocsflowRestClient.docsflow_invoice_parseSchneider(ParseSchneiderInvoiceRequestJS().also {
                       it.content = value
                   })
                   val currentData = editor.getEditor().positionsWidget.getData()
                   result.items.forEach { item ->
                       val position = currentData.items.find { item.article.contains(it.article!!)}
                           ?:return@forEach
                       position.invoiceAmount = item.amount
                       if(item.total != null){
                           position.totalPrice = item.total
                       }
                   }
                   editor.getEditor().positionsWidget.readData(currentData, null)
               }
            }
    }
}
