/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.storage.test

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.common.core.model.rest.ReplaceOrderPositionRestRequest
import com.flinty.docsflow.server.core.order.rest.ReplaceOrderPositionRestHandler
import com.flinty.docsflow.server.core.test.DocsFlowServerTestBase
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.standard.model.ContentType
import com.gridnine.jasmine.common.standard.model.FileData
import org.junit.Assert
import org.junit.Test

class SpecificationPositionReplacementTest:DocsFlowServerTestBase() {
    @Test
    fun testReplacement(){
        val projRef = createProject("Test")
        val suppRef = createSupplier("Supplier")
        var spec1 = Specification()
        spec1.name = "Test specification"
        spec1.status = SpecificationStatus.NEW
        spec1.project = projRef
        spec1.content  = FileData().also {
            it.fileName =""
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
        val specRef = EntityUtils.toReference(spec1)
        var order = findOrder()
        val response = ReplaceOrderPositionRestHandler().doReplace(ReplaceOrderPositionRestRequest().also {
            it.newArticle = "pos1R"
            it.newName = "Position 1R"
            it.oldArticle = "pos1"
            it.orderUid = order.uid
        })
        Assert.assertEquals(1, response.modifiedSpecifications.size)
        Assert.assertEquals(specRef, response.modifiedSpecifications[0])
        spec1 = Storage.get().loadDocument(specRef)!!
        Assert.assertEquals(1, spec1.positions.size)
        Assert.assertEquals("pos1R", spec1.positions[0].article)
        Assert.assertEquals("Position 1R", spec1.positions[0].name)
        Assert.assertEquals("pos1", spec1.positions[0].replacementFor)
        var spec2 = Specification()
        spec2.name = "Test specification 2"
        spec2.status = SpecificationStatus.NEW
        spec2.project = projRef
        spec2.content  = FileData().also {
            it.fileName =""
            it.contentType = ContentType.EXCEL_2007
            it.content = ByteArray(0)
        }
        spec2.positions.add(SpecificationPosition().also {
            it.supplier = suppRef
            it.amount = 1.toBigDecimal()
            it.article = "pos1"
            it.storeAmount = 0.toBigDecimal()
            it.toBeOrdered = 1.toBigDecimal()
            it.unit = PositionUnit.PIECE
            it.name = "Position 1"
        })
        Storage.get().saveDocument(spec2)
        order = findOrder()
        Assert.assertEquals(1, order.positions.size)
        assertEquals(2, order.positions[0].amount)
        Assert.assertEquals("pos1R", order.positions[0].article)
        Assert.assertEquals("Position 1R", order.positions[0].name)
        Assert.assertEquals(1, spec2.positions.size)
        Assert.assertEquals("pos1R", spec2.positions[0].article)
        Assert.assertEquals("Position 1R", spec2.positions[0].name)
        Assert.assertEquals("pos1", spec2.positions[0].replacementFor)

    }
    private fun findOrder(): SpecificationOrder {
        return Storage.get().loadDocument(Storage.get().searchDocuments(SpecificationOrderIndex::class, searchQuery {  }).first().document, true)!!
    }
}