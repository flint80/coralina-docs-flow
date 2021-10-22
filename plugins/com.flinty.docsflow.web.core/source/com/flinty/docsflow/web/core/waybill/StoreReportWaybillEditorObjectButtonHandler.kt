/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.waybill

import com.flinty.docsflow.common.core.model.rest.DownloadOrderRequestJS
import com.flinty.docsflow.common.core.model.rest.GenerateStoreReportRequestJS
import com.flinty.docsflow.common.core.model.ui.OrderEditor
import com.flinty.docsflow.common.core.model.ui.WaybillEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.core.utils.ContentTypeJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool

class StoreReportWaybillEditorObjectButtonHandler: ObjectEditorTool<WaybillEditor> {
    override suspend fun invoke(editor: ObjectEditor<WaybillEditor>) {
        val result = DocsflowRestClient.docsflow_waybill_generateStoreReport(GenerateStoreReportRequestJS().also {
            it.waybillUid = editor.objectUid
        })
        MiscUtilsJS.downloadFile(result.result.fileName, ContentTypeJS.EXCEL, result.result.encodedContent)
    }
}