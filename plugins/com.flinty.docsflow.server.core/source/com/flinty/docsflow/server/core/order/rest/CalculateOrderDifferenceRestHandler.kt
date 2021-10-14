/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.order.rest

import com.flinty.docsflow.common.core.model.domain.SpecificationOrder
import com.flinty.docsflow.common.core.model.rest.CalculateOrderDifferenceRequest
import com.flinty.docsflow.common.core.model.rest.CalculateOrderDifferenceResponse
import com.flinty.docsflow.common.core.model.ui.OrderDifferenceTableVM
import com.flinty.docsflow.common.core.model.ui.ShowOrderDifferenceDialogVM
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class CalculateOrderDifferenceRestHandler:RestHandler<CalculateOrderDifferenceRequest, CalculateOrderDifferenceResponse> {
    override fun service(
        request: CalculateOrderDifferenceRequest,
        ctx: RestOperationContext
    ): CalculateOrderDifferenceResponse {
        val order = Storage.get().loadDocument(SpecificationOrder::class, request.orderUid)!!
        val oldPositions = ArrayList(order.referenceState)
        val response = CalculateOrderDifferenceResponse().also { it.vm = ShowOrderDifferenceDialogVM() }

        order.positions.forEach { pos ->
            val oldPos = oldPositions.find { pos.article == it.article }
            if(oldPos == null){
                response.vm.positions.add(OrderDifferenceTableVM().also {
                    it.uid = pos.uid
                    it.article = pos.article
                    it.newAmount = pos.amount
                })
            } else if (pos.unit != oldPos.unit || pos.amount.compareTo(oldPos.amount) != 0) {
                oldPositions.remove(oldPos)
                response.vm.positions.add(OrderDifferenceTableVM().also {
                    it.uid = pos.uid
                    it.article = pos.article
                    it.newAmount = pos.amount
                    it.oldAmount = oldPos.amount
                })
            } else {
                oldPositions.remove(oldPos)
            }
        }
        oldPositions.forEach { oldPos ->
            response.vm.positions.add(OrderDifferenceTableVM().also {
                it.uid = oldPos.uid
                it.article = oldPos.article
                it.oldAmount = oldPos.amount
            })
        }
        return response
    }
}