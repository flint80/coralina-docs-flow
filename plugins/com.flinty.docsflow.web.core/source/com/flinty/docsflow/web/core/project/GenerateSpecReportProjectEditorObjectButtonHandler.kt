/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.project

import com.flinty.docsflow.common.core.model.rest.GenerateSpecReportRequestJS
import com.flinty.docsflow.common.core.model.ui.ProjectEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.core.utils.ContentTypeJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool

class GenerateSpecReportProjectEditorObjectButtonHandler:
    ObjectEditorTool<ProjectEditor> {
    override suspend fun invoke(editor: ObjectEditor<ProjectEditor>) {
        val result = DocsflowRestClient.docsflow_project_generateSpecReport(GenerateSpecReportRequestJS().also {
            it.projectUid = editor.objectUid
        })
        MiscUtilsJS.downloadFile(result.result!!.fileName, ContentTypeJS.EXCEL, result.result!!.encodedContent)
    }
}