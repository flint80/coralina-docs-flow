/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.order.storage

import com.flinty.docsflow.common.core.model.domain.OrderStatus
import com.flinty.docsflow.common.core.model.domain.SpecificationOrder
import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import com.gridnine.jasmine.server.core.storage.OperationContext
import com.gridnine.jasmine.server.core.storage.StorageInterceptor

class OrderInterceptor : StorageInterceptor {
    override val priority = 1.0
    override fun <D : BaseDocument> onSave(doc: D, context: OperationContext<D>) {
        if (context.globalContext.newObject == doc && doc is SpecificationOrder) {
            val order = doc as SpecificationOrder
            if(order.status == OrderStatus.SENT){
                order.referenceState.clear()
                order.positions.forEach {
                    order.referenceState.add(SerializationProvider.get().clone(it, true))
                }
            }
        }
    }
}
