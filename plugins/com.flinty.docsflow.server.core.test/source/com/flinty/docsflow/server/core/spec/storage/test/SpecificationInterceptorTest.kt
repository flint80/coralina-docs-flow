/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.storage.test

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.server.core.test.DocsFlowServerTestBase
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.standard.model.ContentType
import com.gridnine.jasmine.common.standard.model.FileData
import org.junit.Assert
import org.junit.Test

class SpecificationInterceptorTest: DocsFlowServerTestBase() {
    @Test
    fun testSpecificationInterceptor(){
        val projRef = createProject("Test")
        val suppRef = createSupplier("Supplier")
        val spec = Specification()
        spec.name = "Test specification"
        spec.status = SpecificationStatus.NEW
        spec.project = projRef
        spec.content  = FileData().also {
            it.fileName =""
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

        spec.positions.add(SpecificationPosition().also {
            it.supplier = suppRef
            it.amount = 2.toBigDecimal()
            it.article = "pos2"
            it.storeAmount = 1.toBigDecimal()
            it.toBeOrdered = 1.toBigDecimal()
            it.unit = PositionUnit.PIECE
            it.name = "Position 2"
        })
        Storage.get().saveDocument(spec)
        val specRef = EntityUtils.toReference(spec)
        var order = findOrder()
        run {
            Assert.assertEquals(OrderStatus.DRAFT, order.status)
            Assert.assertNotNull(order.code)
            Assert.assertEquals(suppRef, order.supplier)
            Assert.assertEquals(2, order.positions.size)
            Assert.assertEquals(SpecificationStatus.ORDERED, spec.status)
            run {
                val position = order.positions[0]
                assertEquals(1, position.amount)
                Assert.assertEquals("pos1", position.article)
                Assert.assertEquals("Position 1", position.name)
                Assert.assertEquals(PositionUnit.PIECE, position.unit)
                Assert.assertEquals(1, position.specificationSplits.size)
                val split = position.specificationSplits[0]
                Assert.assertEquals(specRef, split.specification)
                assertEquals(1, split.amount)
            }
            run {
                val position = order.positions[1]
                assertEquals(1, position.amount)
                Assert.assertEquals("pos2", position.article)
                Assert.assertEquals("Position 2", position.name)
                Assert.assertEquals(PositionUnit.PIECE, position.unit)
                Assert.assertEquals(1, position.specificationSplits.size)
                val split = position.specificationSplits[0]
                Assert.assertEquals(specRef, split.specification)
                assertEquals(1, split.amount)
            }
        }
        spec.positions[1].toBeOrdered = 2.toBigDecimal()
        spec.positions[1].amount = 3.toBigDecimal()
        Storage.get().saveDocument(spec)
        order = findOrder()
        run {
            Assert.assertEquals(OrderStatus.DRAFT, order.status)
            Assert.assertNotNull(order.code)
            Assert.assertEquals(suppRef, order.supplier)
            Assert.assertEquals(2, order.positions.size)
            Assert.assertEquals(SpecificationStatus.ORDERED, spec.status)
            run {
                val position = order.positions[1]
                assertEquals(2, position.amount)
                Assert.assertEquals("pos2", position.article)
                Assert.assertEquals("Position 2", position.name)
                Assert.assertEquals(PositionUnit.PIECE, position.unit)
                Assert.assertEquals(1, position.specificationSplits.size)
                val split = position.specificationSplits[0]
                Assert.assertEquals(specRef, split.specification)
                assertEquals(2, split.amount)
            }
        }
        spec.positions.removeAt(1)
        Storage.get().saveDocument(spec)
        order = findOrder()
        run {
            Assert.assertEquals(OrderStatus.DRAFT, order.status)
            Assert.assertNotNull(order.code)
            Assert.assertEquals(suppRef, order.supplier)
            Assert.assertEquals(1, order.positions.size)
            Assert.assertEquals(SpecificationStatus.ORDERED, spec.status)
            run {
                val position = order.positions[0]
                assertEquals(1, position.amount)
                Assert.assertEquals("pos1", position.article)
                Assert.assertEquals("Position 1", position.name)
                Assert.assertEquals(PositionUnit.PIECE, position.unit)
                Assert.assertEquals(1, position.specificationSplits.size)
                val split = position.specificationSplits[0]
                Assert.assertEquals(specRef, split.specification)
                assertEquals(1, split.amount)
            }
        }
        Storage.get().deleteDocument(spec)
        Assert.assertEquals(0, Storage.get().searchDocuments(SpecificationOrderIndex::class, searchQuery {  }).size)
    }

    private fun findOrder():SpecificationOrder{
        return Storage.get().loadDocument(Storage.get().searchDocuments(SpecificationOrderIndex::class, searchQuery {  }).first().document, true)!!
    }
}