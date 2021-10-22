/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.web.core.spec

import com.flinty.docsflow.common.core.model.domain.SpecificationIndexJS
import com.flinty.docsflow.common.core.model.rest.CheckSpecificationSuppliersRequestJS
import com.flinty.docsflow.common.core.model.rest.ReimportSpecificationRequestJS
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersDialog
import com.flinty.docsflow.common.core.model.ui.SpecificationEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.common.standard.rest.FileDataDTJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils


class ReimportSpecificationEditorObjectButtonHandler : ObjectEditorTool<SpecificationEditor> {
    override suspend fun invoke(editor: ObjectEditor<SpecificationEditor>) {
        val data = MiscUtilsJS.uploadFile() ?: return
        val result =
            DocsflowRestClient.docsflow_specification_checkSuppliers(CheckSpecificationSuppliersRequestJS().also {
                it.content = data.content
            })
        result.dialogVM!!.project = editor.getEditor().projectWidget.getValue()
        if(result.dialogVM!!.suppliers.isEmpty()){
            DocsflowRestClient.docsflow_specification_reimport(ReimportSpecificationRequestJS().also {
                it.fileData = FileDataDTJS().also { fd->
                    fd.encodedContent =data.content
                    fd.fileName = data.fileName
                }
                it.dialogVM = result.dialogVM!!
                it.specificationUid = editor.objectUid
            })
            MainFrame.get()
                .openTab(OpenObjectData(SpecificationIndexJS.objectId, editor.objectUid, null, true))
            return
        }
        val dialog = ProcessSuppliersDialog()
        dialog.projectWidget.setReadonly(true)
        dialog.readData(result.dialogVM!!, result.dialogVS)
        WebUiLibraryAdapter.get().showDialog(dialog) {
            title = "Обработка поставщиков"
            button {
                displayName = WebMessages.apply
                handler = {
                    it.close()
                    val response = DocsflowRestClient.docsflow_specification_reimport(ReimportSpecificationRequestJS().also {
                        it.fileData = FileDataDTJS().also { fd->
                            fd.encodedContent =data.content
                            fd.fileName = data.fileName
                        }
                        it.dialogVM = result!!.dialogVM
                    })
                    if(StandardUiUtils.hasValidationErrors(response.dialogVV)){
                        dialog.showValidation(response.dialogVV)
                    } else {
                        it.close()
                        MainFrame.get()
                            .openTab(OpenObjectData(SpecificationIndexJS.objectId, editor.objectUid, null, true))
                    }
                }
            }
            cancelButton()
        }
    }

}