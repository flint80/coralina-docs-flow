/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.web.core.supplier

import com.flinty.docsflow.common.core.model.domain.SupplierIndexJS
import com.flinty.docsflow.common.core.model.domain.UserAccountIndexJS
import com.flinty.docsflow.common.core.model.rest.CreateSupplierRequestJS
import com.flinty.docsflow.common.core.model.ui.NewSupplierEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.list.ListLinkButtonHandler
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class CreateNewSupplierListButtonHandler : ListLinkButtonHandler<BaseIdentityJS> {
    override suspend fun invoke(selected: List<BaseIdentityJS>) {
        val editor = NewSupplierEditor()
        WebUiLibraryAdapter.get().showDialog(editor){
            title = "Создание нового поставщика"
            button {
                displayName = WebMessages.apply
                handler = {
                    val vm = it.getContent().getData()
                    val response = DocsflowRestClient.docsflow_supplier_create(CreateSupplierRequestJS().also { resp ->
                        resp.vm = vm
                    })
                    if(StandardUiUtils.hasValidationErrors(response.vv)){
                        it.getContent().showValidation(response.vv)
                        response.vv
                    } else{
                        it.close()
                        MainFrame.get().openTab(OpenObjectData(SupplierIndexJS.objectId, response.objectUid!!, null, true))
                    }
                }
            }
            cancelButton()
        }
    }

}