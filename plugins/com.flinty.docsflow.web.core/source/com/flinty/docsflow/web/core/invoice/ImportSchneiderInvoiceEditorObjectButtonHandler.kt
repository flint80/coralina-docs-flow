/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.invoice

import com.flinty.docsflow.common.core.model.domain.StandardInvoiceColumnTypeJS
import com.flinty.docsflow.common.core.model.rest.ParseSchneiderInvoiceRequestJS
import com.flinty.docsflow.common.core.model.rest.ParseStandardInvoiceRequestJS
import com.flinty.docsflow.common.core.model.ui.InvoiceEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.common.core.model.BaseVMJS
import com.gridnine.jasmine.common.core.model.BaseVSJS
import com.gridnine.jasmine.common.core.model.BaseVVJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebTag
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import com.gridnine.jasmine.web.standard.widgets.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await

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
