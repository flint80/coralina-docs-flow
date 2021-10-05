/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.web.core.project

import com.flinty.docsflow.common.core.model.domain.ProjectIndexJS
import com.flinty.docsflow.common.core.model.rest.CreateProjectRequestJS
import com.flinty.docsflow.common.core.model.ui.NewProjectEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.list.ListLinkButtonHandler
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class CreateNewProjectListButtonHandler : ListLinkButtonHandler<BaseIdentityJS> {
    override suspend fun invoke(selected: List<BaseIdentityJS>) {
        val editor = NewProjectEditor()
        WebUiLibraryAdapter.get().showDialog(editor){
            title = "Создание нового проекта"
            button {
                displayName = WebMessages.apply
                handler = {
                    val vm = it.getContent().getData()
                    val response = DocsflowRestClient.docsflow_project_create(CreateProjectRequestJS().also { resp ->
                        resp.vm = vm
                    })
                    if(StandardUiUtils.hasValidationErrors(response.vv)){
                        it.getContent().showValidation(response.vv)
                        response.vv
                    } else{
                        it.close()
                        MainFrame.get().openTab(OpenObjectData(ProjectIndexJS.objectId, response.objectUid!!, null, true))
                    }
                }
            }
            cancelButton()
        }
    }

}