/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.gridnine.jasmine.server.core.storage.IndexHandler

class SpecificationIndexHandler : IndexHandler<Specification, SpecificationIndex> {
    override val documentClass = Specification::class
    override val indexClass = SpecificationIndex::class
    override fun createIndexes(doc: Specification): List<SpecificationIndex> {
        val idx = SpecificationIndex()
        idx.uid = doc.uid
        idx.name = doc.name
        idx.status = doc.status
        idx.project = doc.project
        return arrayListOf(idx)
    }
}