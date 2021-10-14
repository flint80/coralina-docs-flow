/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.surplus.storage

import com.flinty.docsflow.common.core.model.domain.Surplus
import com.flinty.docsflow.common.core.model.domain.SurplusIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler

class SurplusIndexHandler:IndexHandler<Surplus, SurplusIndex> {
    override val documentClass = Surplus::class
    override val indexClass = SurplusIndex::class

    override fun createIndexes(doc: Surplus): List<SurplusIndex> {
        val index = SurplusIndex()
        index.uid = doc.uid
        index.amount = doc.amount
        index.article = doc.article
        index.invoice  = doc.invoice
        index.name = doc.name
        index.specificationOrder = doc.order
        index.status = doc.status
        index.unit = doc.unit
        return arrayListOf(index)
    }
}