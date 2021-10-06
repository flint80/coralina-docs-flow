/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.rest

import com.flinty.docsflow.common.core.model.domain.SupplierSpellVariantIndex
import com.flinty.docsflow.common.core.model.rest.CheckSpecificationSuppliersRequest
import com.flinty.docsflow.common.core.model.rest.CheckSpecificationSuppliersResponse
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersDialogVM
import com.flinty.docsflow.common.core.model.ui.ProcessSuppliersTableVM
import com.flinty.docsflow.server.core.spec.parser.StandardSpecificationParser
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import java.util.*

class CheckSpecificationSuppliersRestHandler:RestHandler<CheckSpecificationSuppliersRequest, CheckSpecificationSuppliersResponse> {
    override fun service(
        request: CheckSpecificationSuppliersRequest,
        ctx: RestOperationContext
    ): CheckSpecificationSuppliersResponse {
        val items = when(request.specType){
            SpecificationFormat.STANDARD -> StandardSpecificationParser.parse(Base64.getDecoder().decode(request.content))
        }
        val unknownSuppliers =items.map { it.supplier }.distinct().filter {
            Storage.get().findUniqueDocumentReference(SupplierSpellVariantIndex::class, SupplierSpellVariantIndex.spellVariantProperty, it.toLowerCase()) == null
        }.sorted()
        if(unknownSuppliers.isEmpty()){
            return CheckSpecificationSuppliersResponse().also { it.allSuppliersExist = true }
        }
        return CheckSpecificationSuppliersResponse().also {
            it.allSuppliersExist = false
            it.dialogVM = ProcessSuppliersDialogVM().also {dialogVM ->
                unknownSuppliers.forEach { supplier ->
                    dialogVM.suppliers.add(ProcessSuppliersTableVM().also { record ->
                        record.uid = TextUtils.generateUid()
                        record.createNew = true
                        record.supplierName = supplier
                    })
                }
            }
        }
    }
}