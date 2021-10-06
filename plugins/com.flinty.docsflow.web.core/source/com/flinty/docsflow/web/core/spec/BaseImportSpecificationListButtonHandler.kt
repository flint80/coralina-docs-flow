/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.web.core.spec

import com.flinty.docsflow.common.core.model.domain.SpecificationIndexJS
import com.flinty.docsflow.common.core.model.rest.CheckSpecificationSuppliersRequestJS
import com.flinty.docsflow.common.core.model.rest.ImportSpecificationRequestJS
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersDialog
import com.flinty.docsflow.server.core.spec.rest.SpecificationFormatJS
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.list.ListLinkButtonHandler
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

abstract class BaseImportSpecificationListButtonHandler : ListLinkButtonHandler<BaseIdentityJS> {
    override suspend fun invoke(selected: List<BaseIdentityJS>) {
        val data = MiscUtilsJS.uploadFile() ?: return
        val result =
            DocsflowRestClient.docsflow_specification_checkSuppliers(CheckSpecificationSuppliersRequestJS().also {
                it.specType = getSpecFormat()
                it.content = data.content
            })
        if (result.allSuppliersExist == true) {
            val result = DocsflowRestClient.docsflow_specification_import(ImportSpecificationRequestJS().also {
                it.specType = getSpecFormat()
                it.content = data.content
            })
            MainFrame.get().openTab(OpenObjectData(SpecificationIndexJS.objectId, result.objectUid!!, null, true))
            return
        }
        val dialog = ProcessSuppliersDialog()
        dialog.readData(result.dialogVM!!, null)
        WebUiLibraryAdapter.get().showDialog(dialog) {
            title = "Обработка поставщиков"
            button {
                displayName = WebMessages.apply
                handler = {
                    val result = DocsflowRestClient.docsflow_specification_import(ImportSpecificationRequestJS().also {
                        it.specType = getSpecFormat()
                        it.content = data.content
                    })
                    if(StandardUiUtils.hasValidationErrors(result.dialogVV)){
                        dialog.showValidation(result.dialogVV)
                    } else {
                        it.close()
                        MainFrame.get().openTab(OpenObjectData(SpecificationIndexJS.objectId, result.objectUid!!, null, true))
                    }
                }
            }
            cancelButton()
        }
    }

    abstract fun getSpecFormat(): SpecificationFormatJS
}