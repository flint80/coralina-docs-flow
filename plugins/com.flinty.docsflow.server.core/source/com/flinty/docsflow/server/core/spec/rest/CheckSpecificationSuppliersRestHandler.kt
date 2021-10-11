/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.rest

import com.flinty.docsflow.common.core.model.domain.ProjectIndex
import com.flinty.docsflow.common.core.model.domain.SupplierSpellVariantIndex
import com.flinty.docsflow.common.core.model.rest.CheckSpecificationSuppliersRequest
import com.flinty.docsflow.common.core.model.rest.CheckSpecificationSuppliersResponse
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersDialogVM
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersDialogVS
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersTableVM
import com.flinty.docsflow.server.core.spec.common.SpecificationUtils
import com.flinty.docsflow.server.core.spec.parser.StandardSpecificationParser
import com.gridnine.jasmine.common.core.model.GeneralSelectBoxConfiguration
import com.gridnine.jasmine.common.core.storage.SortOrder
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.core.utils.CommonUiUtils
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import java.util.*

class CheckSpecificationSuppliersRestHandler:RestHandler<CheckSpecificationSuppliersRequest, CheckSpecificationSuppliersResponse> {
    override fun service(
        request: CheckSpecificationSuppliersRequest,
        ctx: RestOperationContext
    ): CheckSpecificationSuppliersResponse {
        val items = StandardSpecificationParser.parse(Base64.getDecoder().decode(request.content))
        val unknownSuppliers =items.map { it.supplier }.distinct().filter {
            Storage.get().findUniqueDocumentReference(SupplierSpellVariantIndex::class, SupplierSpellVariantIndex.spellVariantProperty, it.toLowerCase()) == null
        }.sorted()
        val result = CheckSpecificationSuppliersResponse()
        val dialogVM = ProcessSuppliersDialogVM()
        result.dialogVM  = dialogVM
        unknownSuppliers.forEach { supplier ->
            dialogVM.suppliers.add(ProcessSuppliersTableVM().also { record ->
                record.uid = TextUtils.generateUid()
                record.createNew = true
                record.supplierName = supplier
            })
        }
        val dialogVS = ProcessSuppliersDialogVS()
        result.dialogVS = dialogVS
        dialogVS.project = SpecificationUtils.getProjectSelectConfiguration()
        return result
    }
}