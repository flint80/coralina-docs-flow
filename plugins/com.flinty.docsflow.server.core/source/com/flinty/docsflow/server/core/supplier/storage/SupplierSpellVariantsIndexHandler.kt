/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.supplier.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.gridnine.jasmine.server.core.storage.IndexHandler

class SupplierSpellVariantsIndexHandler : IndexHandler<Supplier, SupplierSpellVariantIndex> {
    override val documentClass = Supplier::class
    override val indexClass = SupplierSpellVariantIndex::class
    override fun createIndexes(doc: Supplier): List<SupplierSpellVariantIndex> {
        return doc.spellVariants.withIndex().map {
            val idx = SupplierSpellVariantIndex()
            idx.uid = "${doc.uid}_${it.index}"
            idx.spellVariant = it.value.toLowerCase()
            idx
        }
    }
}