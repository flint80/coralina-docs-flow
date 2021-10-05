/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.supplier.storage

import com.flinty.docsflow.common.core.model.domain.Supplier
import com.flinty.docsflow.common.core.model.domain.SupplierIndex
import com.flinty.docsflow.common.core.model.domain.UserAccount
import com.flinty.docsflow.common.core.model.domain.UserAccountIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler

class SupplierIndexHandler : IndexHandler<Supplier, SupplierIndex> {
    override val documentClass = Supplier::class
    override val indexClass = SupplierIndex::class
    override fun createIndexes(doc: Supplier): List<SupplierIndex> {
        val idx = SupplierIndex()
        idx.uid = doc.uid
        idx.name = doc.name
        return arrayListOf(idx)
    }
}