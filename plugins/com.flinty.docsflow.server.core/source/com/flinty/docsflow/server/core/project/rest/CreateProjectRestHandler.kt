/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.project.rest

import com.flinty.docsflow.common.core.model.domain.Project
import com.flinty.docsflow.common.core.model.rest.CreateProjectRequest
import com.flinty.docsflow.common.core.model.rest.CreateProjectResponse
import com.flinty.docsflow.common.core.model.ui.NewProjectEditorVV
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.helpers.ValidationUtils

class CreateProjectRestHandler :RestHandler<CreateProjectRequest,CreateProjectResponse>{
    override fun service(request: CreateProjectRequest, ctx: RestOperationContext): CreateProjectResponse {
        val result = CreateProjectResponse()
        val validation = NewProjectEditorVV()
        if(TextUtils.isBlank(request.vm.name)){
            validation.name = StandardL10nMessagesFactory.Empty_field()
        }
        if(ValidationUtils.hasValidationErrors(validation)){
            result.vv = validation
            return result
        }
        val supplier = Project()
        supplier.name = request.vm.name
        Storage.get().saveDocument(supplier)
        result.objectUid = supplier.uid
        return result
    }
}