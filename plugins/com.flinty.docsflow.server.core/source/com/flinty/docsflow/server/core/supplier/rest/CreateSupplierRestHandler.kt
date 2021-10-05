/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.supplier.rest

import com.flinty.docsflow.common.core.model.domain.Supplier
import com.flinty.docsflow.common.core.model.domain.SupplierType
import com.flinty.docsflow.common.core.model.rest.CreateSupplierRequest
import com.flinty.docsflow.common.core.model.rest.CreateSupplierResponse
import com.flinty.docsflow.common.core.model.ui.NewSupplierEditorVV
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.utils.DigestUtils
import com.gridnine.jasmine.server.standard.helpers.ValidationUtils

class CreateSupplierRestHandler :RestHandler<CreateSupplierRequest,CreateSupplierResponse>{
    override fun service(request: CreateSupplierRequest, ctx: RestOperationContext): CreateSupplierResponse {
        val result = CreateSupplierResponse()
        val validation = NewSupplierEditorVV()
        if(TextUtils.isBlank(request.vm.name)){
            validation.name = StandardL10nMessagesFactory.Empty_field()
        }
        if(ValidationUtils.hasValidationErrors(validation)){
            result.vv = validation
            return result
        }
        val supplier = Supplier()
        supplier.name = request.vm.name
        supplier.type = SupplierType.STANDARD
        supplier.spellVariants.add(supplier.name!!.toLowerCase())
        Storage.get().saveDocument(supplier)
        result.objectUid = supplier.uid
        return result
    }
}