/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.order.storage

import com.flinty.docsflow.common.core.model.domain.SpecificationOrder
import com.flinty.docsflow.common.core.model.domain.SpecificationOrderIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler

class OrderIndexHandler : IndexHandler<SpecificationOrder, SpecificationOrderIndex> {
    override val documentClass = SpecificationOrder::class
    override val indexClass = SpecificationOrderIndex::class
    override fun createIndexes(doc: SpecificationOrder): List<SpecificationOrderIndex> {
        val idx = SpecificationOrderIndex()
        idx.uid = doc.uid
        idx.code = doc.code
        idx.status = doc.status
        idx.supplier = doc.supplier
        return arrayListOf(idx)
    }
}