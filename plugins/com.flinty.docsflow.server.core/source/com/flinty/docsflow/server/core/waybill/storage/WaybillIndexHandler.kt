/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.waybill.storage

import com.flinty.docsflow.common.core.model.domain.Waybill
import com.flinty.docsflow.common.core.model.domain.WaybillIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler

class WaybillIndexHandler:IndexHandler<Waybill, WaybillIndex> {
    override val documentClass = Waybill::class
    override val indexClass = WaybillIndex::class

    override fun createIndexes(doc: Waybill): List<WaybillIndex> {
        val index = WaybillIndex()
        index.uid = doc.uid
        index.invoice = doc.invoice
        index.status = doc.status
        index.supplier = doc.supplier
        index.waybillNumber = doc.number
        return arrayListOf(index)
    }
}