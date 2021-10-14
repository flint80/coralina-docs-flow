/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.storage.test

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.server.core.invoice.common.InvoiceHelper
import com.flinty.docsflow.server.core.test.DocsFlowServerTestBase
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.standard.model.ContentType
import com.gridnine.jasmine.common.standard.model.FileData
import org.junit.Assert
import org.junit.Test

class BasicInvoiceTest : DocsFlowServerTestBase() {
    @Test
    fun testBasicInvoiceCreation() {
        val projRef = createProject("Test")
        val suppRef = createSupplier("Supplier")
        val spec = Specification()
        spec.name = "Test specification"
        spec.status = SpecificationStatus.NEW
        spec.project = projRef
        spec.content = FileData().also {
            it.fileName = ""
            it.contentType = ContentType.EXCEL_2007
            it.content = ByteArray(0)
        }
        spec.positions.add(SpecificationPosition().also {
            it.supplier = suppRef
            it.amount = 1.toBigDecimal()
            it.article = "pos1"
            it.storeAmount = 0.toBigDecimal()
            it.toBeOrdered = 1.toBigDecimal()
            it.unit = PositionUnit.PIECE
            it.name = "Position 1"
        })
        Storage.get().saveDocument(spec)
        var order = findOrder()
        val orderRef = EntityUtils.toReference(order)
        val invoice = InvoiceHelper.createInvoice(order)
        invoice.status = InvoiceStatus.FIXED
        invoice.number = "001"
        invoice.positions[0].invoiceAmount = 2.toBigDecimal()
        Storage.get().saveDocument(invoice)
        val surplus = findSurplus()
        val surplusRef = EntityUtils.toReference(surplus)
        val invoiceRef = EntityUtils.toReference(invoice)
        run{
            Assert.assertEquals(InvoiceStatus.FIXED, invoice.status)
            Assert.assertEquals(1, invoice.positions.size)
            val pos = invoice.positions[0]
            assertEquals(1, pos.orderAmount)
            Assert.assertEquals(surplusRef, pos.surplus)
            Assert.assertEquals(0, pos.surplusSplits.size)
        }
        run{
            Assert.assertEquals(SurplusStatus.DRAFT, surplus.status)
            assertEquals(1, surplus.amount)
            Assert.assertEquals("pos1", surplus.article)
            Assert.assertEquals(invoiceRef, surplus.invoice)
            Assert.assertEquals("Position 1", surplus.name)
            Assert.assertEquals(orderRef, surplus.order)
            Assert.assertEquals(PositionUnit.PIECE, surplus.unit)
            Assert.assertEquals(0, surplus.splits.size)
        }
    }
    private fun findSurplus(): Surplus {
        return Storage.get().loadDocument(Storage.get().searchDocuments(SurplusIndex::class, searchQuery {
            where { eq(SurplusIndex.statusProperty, SurplusStatus.DRAFT) }
        }).first().document, true)!!
    }

    private fun findOrder(): SpecificationOrder {
        return Storage.get().loadDocument(Storage.get().searchDocuments(SpecificationOrderIndex::class, searchQuery {
            where { eq(SpecificationOrderIndex.statusProperty, OrderStatus.DRAFT) }
        }).first().document, true)!!
    }
}