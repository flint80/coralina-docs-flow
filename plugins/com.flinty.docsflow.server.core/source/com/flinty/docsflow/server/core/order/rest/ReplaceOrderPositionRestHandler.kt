/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.order.rest

import com.flinty.docsflow.common.core.model.domain.Specification
import com.flinty.docsflow.common.core.model.domain.SpecificationOrder
import com.flinty.docsflow.common.core.model.rest.ReplaceOrderPositionRestRequest
import com.flinty.docsflow.common.core.model.rest.ReplaceOrderPositionRestResponse
import com.flinty.docsflow.common.core.model.ui.ReplaceOrderPositionDialogVV
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.model.l10n.CoreServerL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.helpers.ValidationUtils

class ReplaceOrderPositionRestHandler:RestHandler<ReplaceOrderPositionRestRequest, ReplaceOrderPositionRestResponse> {
    override fun service(
        request: ReplaceOrderPositionRestRequest,
        ctx: RestOperationContext
    ): ReplaceOrderPositionRestResponse {
        return doReplace(request)

    }

    fun doReplace(request: ReplaceOrderPositionRestRequest): ReplaceOrderPositionRestResponse {
        val order = Storage.get().loadDocument(SpecificationOrder::class, request.orderUid)!!
        val specs = arrayListOf<ObjectReference<Specification>>()
        val vv = ReplaceOrderPositionDialogVV()
        if(request.newArticle == null){
            vv.newArticle = StandardL10nMessagesFactory.Empty_field()
        }
        if(request.newName == null){
            vv.newName = StandardL10nMessagesFactory.Empty_field()
        }
        if(ValidationUtils.hasValidationErrors(vv)){
            return ReplaceOrderPositionRestResponse().also {
                it.vv = vv
            }
        }
        Storage.get().executeInTransaction {
            val pos = order.positions.find { request.oldArticle == it.article }!!
            pos.replacementFor = pos.article
            pos.article = request.newArticle!!
            pos.name  = request.newName!!
            Storage.get().saveDocument(order)
            pos.specificationSplits.forEach { specSplit ->
                val spec = Storage.get().loadDocument(specSplit.specification)!!
                val specPos = spec.positions.find { it.article == request.oldArticle }
                if(specPos != null){
                    specPos.article = request.newArticle!!
                    specPos.name = request.newName!!
                    specPos.replacementFor = request.oldArticle
                    Storage.get().saveDocument(spec, skipInterceptors = true)
                    specs.add(EntityUtils.toReference(spec))
                }
            }

        }
        return ReplaceOrderPositionRestResponse().also {
            it.modifiedSpecifications.addAll(specs)
        }
    }
}