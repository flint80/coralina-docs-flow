/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.waybill.storage.test

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.server.core.invoice.common.InvoiceHelper
import com.flinty.docsflow.server.core.test.DocsFlowServerTestBase
import com.flinty.docsflow.server.core.waybill.storage.WaybillHelper
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.standard.model.ContentType
import com.gridnine.jasmine.common.standard.model.FileData
import org.junit.Assert
import org.junit.Test

class BasicWaybillTest : DocsFlowServerTestBase() {

    @Test
    fun testBasicOperations() {
        val projRef = createProject("Test")
        val suppRef = createSupplier("Supplier")
        val spec1 = Specification()
        spec1.name = "Test specification"
        spec1.status = SpecificationStatus.NEW
        spec1.project = projRef
        spec1.content = FileData().also {
            it.fileName = ""
            it.contentType = ContentType.EXCEL_2007
            it.content = ByteArray(0)
        }
        spec1.positions.add(SpecificationPosition().also {
            it.supplier = suppRef
            it.amount = 1.toBigDecimal()
            it.article = "pos1"
            it.storeAmount = 0.toBigDecimal()
            it.toBeOrdered = 1.toBigDecimal()
            it.unit = PositionUnit.PIECE
            it.name = "Position 1"
        })
        Storage.get().saveDocument(spec1)
        val order = findOrder()
        val orderRef = EntityUtils.toReference(order)
        order.status = OrderStatus.FIXED
        Storage.get().saveDocument(order)
        var invoice = InvoiceHelper.createInvoice(order)
        invoice.status = InvoiceStatus.FIXED
        invoice.number = "001"
        invoice.positions[0].invoiceAmount = 4.toBigDecimal()
        Storage.get().saveDocument(invoice)
        val spec2 = Specification()
        spec2.name = "Test specification 2"
        spec2.status = SpecificationStatus.NEW
        spec2.project = projRef
        spec2.content = FileData().also {
            it.fileName = ""
            it.contentType = ContentType.EXCEL_2007
            it.content = ByteArray(0)
        }
        spec2.positions.add(SpecificationPosition().also {
            it.supplier = suppRef
            it.amount = 2.toBigDecimal()
            it.article = "pos1"
            it.storeAmount = 0.toBigDecimal()
            it.toBeOrdered = 2.toBigDecimal()
            it.unit = PositionUnit.PIECE
            it.name = "Position 1"
        })
        Storage.get().saveDocument(spec2)
        var surplus = findSurplus()
        val surplusRef = EntityUtils.toReference(surplus)
        val invoiceRef = EntityUtils.toReference(invoice)
        var order2 = findOrder()
        val order2Ref = EntityUtils.toReference(order2)
        invoice = Storage.get().loadDocument(invoiceRef)!!
        run{
            Assert.assertEquals(InvoiceStatus.FIXED, invoice.status)
            Assert.assertEquals(1, invoice.positions.size)
            val pos = invoice.positions[0]
            assertEquals(1, pos.orderAmount)
            Assert.assertEquals(surplusRef, pos.surplus)
            Assert.assertEquals(1, pos.surplusSplits.size)
            val split = pos.surplusSplits[0]
            Assert.assertEquals(order2Ref, split.order)
            assertEquals(2, split.amount)
        }
        run{
            Assert.assertEquals(SurplusStatus.DRAFT, surplus.status)
            assertEquals(3, surplus.amount)
            Assert.assertEquals("pos1", surplus.article)
            Assert.assertEquals(invoiceRef, surplus.invoice)
            Assert.assertEquals("Position 1", surplus.name)
            Assert.assertEquals(orderRef, surplus.order)
            Assert.assertEquals(PositionUnit.PIECE, surplus.unit)
            Assert.assertEquals(1, surplus.splits.size)
            val split = surplus.splits[0]
            Assert.assertEquals(order2Ref, split.order)
            assertEquals(2, split.amount)
        }
        val waybill = WaybillHelper.createWaybill(invoice)
        waybill.positions[0].amount = 4.toBigDecimal()
        waybill.status = WaybillStatus.FIXED
        Storage.get().saveDocument(waybill)
        surplus = findSurplus()
        Assert.assertEquals(SurplusStatus.FIXED, surplus.status)
        invoice = Storage.get().loadDocument(invoiceRef)!!
        Assert.assertEquals(InvoiceStatus.FIXED, invoice.status)
        Assert.assertEquals(1, waybill.positions.size)
        Assert.assertEquals(2, waybill.positions[0].specificationsSplit.size)
        run {
            val specData = waybill.positions[0].specificationsSplit[0]
            assertEquals(1, specData.amount)
            Assert.assertEquals(spec1.uid , specData.specification.uid)
        }
        run {
            val specData = waybill.positions[0].specificationsSplit[1]
            assertEquals(2, specData.amount)
            Assert.assertEquals(spec2.uid , specData.specification.uid)
        }

        assertEquals(1, waybill.positions[0].storeAmount!!)
    }

    private fun findSurplus(): Surplus {
        return Storage.get().loadDocument(Storage.get().searchDocuments(SurplusIndex::class, searchQuery {
        }).first().document, true)!!
    }

    private fun findOrder(): SpecificationOrder {
        return Storage.get().loadDocument(Storage.get().searchDocuments(SpecificationOrderIndex::class, searchQuery {
            where { eq(SpecificationOrderIndex.statusProperty, OrderStatus.DRAFT) }
        }).first().document, true)!!
    }


}