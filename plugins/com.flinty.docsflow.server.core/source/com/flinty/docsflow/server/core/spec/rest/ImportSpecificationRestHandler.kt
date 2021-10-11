/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.rest

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.common.core.model.rest.ImportSpecificationRequest
import com.flinty.docsflow.common.core.model.rest.ImportSpecificationResponse
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersDialogVV
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersTableVV
import com.flinty.docsflow.server.core.spec.parser.StandardSpecificationParser
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.CommonUiUtils
import com.gridnine.jasmine.common.standard.model.ContentType
import com.gridnine.jasmine.common.standard.model.FileData
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.model.SequenceNumberGenerator
import java.util.*

class ImportSpecificationRestHandler : RestHandler<ImportSpecificationRequest, ImportSpecificationResponse> {
    override fun service(request: ImportSpecificationRequest, ctx: RestOperationContext): ImportSpecificationResponse {
        val response = ImportSpecificationResponse()
        val dialogVM = request.dialogVM!!
        val dialogVV = ProcessSuppliersDialogVV()
        response.dialogVV = dialogVV
        var hasError = false
        if (dialogVM.project == null) {
            dialogVV.project = StandardL10nMessagesFactory.Empty_field()
            hasError = true
        }
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
        val content = Base64.getDecoder().decode(request.fileData.encodedContent)
        val items = StandardSpecificationParser.parse(content)
        val result = Specification()
        result.content = FileData().also {
            it.content = content
            it.contentType = ContentType.EXCEL_2007
            it.fileName = request.fileData.fileName
        }
        result.project = CommonUiUtils.toObjectReference(dialogVM.project)!!
        val project = Storage.get().loadDocument(result.project)
        val pref = "${project!!.code}-S"
        result.code = "${pref}-${SequenceNumberGenerator.get().incrementAndGet(pref)}"
        result.name = request.fileData.fileName
        items.forEach { item ->
            val position = SpecificationPosition()
            position.amount = item.amount
            position.amountNote = item.amountNote
            position.article = item.article
            position.storeAmount = item.storeAmount
            position.name = item.name
            position.supplier = Storage.get().findUniqueDocumentReference(
                SupplierSpellVariantIndex::class,
                SupplierSpellVariantIndex.spellVariantProperty,
                item.supplier.toLowerCase()
            ) ?: throw Xeption.forDeveloper("Unable to find supplier for code ${item.supplier}")
            position.toBeOrdered = item.toBeOrdered
            position.unit = item.unit
            position.unitNote = item.unitNote
            result.positions.add(position)
        }
        result.status = SpecificationStatus.NEW
        Storage.get().saveDocument(result)
        response.objectUid = result.uid
        return response
    }
}