/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.rest

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.common.core.model.rest.ReimportSpecificationRequest
import com.flinty.docsflow.common.core.model.rest.ReimportSpecificationResponse
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersDialogVV
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersTableVV
import com.flinty.docsflow.server.core.spec.parser.StandardSpecificationParser
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import java.math.BigDecimal
import java.util.*

class ReimportSpecificationRestHandler:RestHandler<ReimportSpecificationRequest, ReimportSpecificationResponse> {
    override fun service(
        request: ReimportSpecificationRequest,
        ctx: RestOperationContext
    ): ReimportSpecificationResponse {
        val response = ReimportSpecificationResponse()
        val dialogVM = request.dialogVM!!
        val dialogVV = ProcessSuppliersDialogVV()
        response.dialogVV = dialogVV
        var hasError = false
        val errors = dialogVM.suppliers.map { !it.createNew && it.existingSupplier == null }
        errors.withIndex().forEach { errorEntry ->
            dialogVV.suppliers.add(ProcessSuppliersTableVV().also {
                it.uid = dialogVM.suppliers[errorEntry.index].uid
                val hHasError = errors[errorEntry.index]
                hasError = hasError || hHasError
                it.existingSupplier = if (hHasError) StandardL10nMessagesFactory.Empty_field() else null
            }
            )
        }
        if (hasError) {
            return response
        }
        dialogVM.suppliers.forEach {
            if (it.createNew) {
                val supplier = Supplier()
                supplier.name = it.supplierName
                supplier.spellVariants.add(it.supplierName!!.toLowerCase())
                supplier.type = SupplierType.STANDARD
                Storage.get().saveDocument(supplier)
            }
        }
        val spec = Storage.get().loadDocument(Specification::class, request.specificationUid)!!
        val content = Base64.getDecoder().decode(request.fileData.encodedContent)
        val positions = StandardSpecificationParser.parse(content)
        val newPositions = arrayListOf<SpecificationPosition>()
        positions.withIndex().forEach { (index, value) ->
            val position = spec.positions.find { it.article == value.article } ?: SpecificationPosition().also {
                it.article = value.article
            }
            newPositions.add(position)
            position.amount = value.amount
            position.amountNote = value.amountNote
            position.toBeOrdered = value.toBeOrdered
            position.name = value.name
            position.storeAmount = value.storeAmount
            val supplier = Storage.get().findUniqueDocumentReference(
                SupplierSpellVariantIndex::class,
                SupplierSpellVariantIndex.spellVariantProperty,
                value.supplier.toLowerCase()
            ) ?: throw Xeption.forDeveloper("Unable to find supplier for code ${value.supplier}")
            position.supplier = supplier
            position.unit = value.unit
            position.unitNote = value.unitNote
        }
        spec.positions.clear()
        spec.positions.addAll(newPositions)
        spec.content.content = content
        spec.content.fileName = request.fileData.fileName
        Storage.get().saveDocument(spec)
        return ReimportSpecificationResponse()
    }
}