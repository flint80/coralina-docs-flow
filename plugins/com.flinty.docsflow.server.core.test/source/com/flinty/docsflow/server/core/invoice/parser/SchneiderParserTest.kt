/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.parser

import com.flinty.docsflow.server.core.invoice.rest.ParseSchneiderInvoiceRestHandler
import com.gridnine.jasmine.common.core.test.TestBase
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class SchneiderParserTest:TestBase() {
    @Test
    fun testParsing(){
        val content = getContent("testdata/schneider.txt")!!
        val result = ParseSchneiderInvoiceRestHandler().parseFile(content.toString(
            Charsets.UTF_8))
        Assert.assertEquals(69, result.size)
        result.forEach {
            Assert.assertNotNull(it.article)
            Assert.assertNotNull("${it.article} amount= null", it.amount)
            if("VZ8" != it.article) {
                Assert.assertNotNull("${it.article} total= null", it.total)
            }
        }
        assertEquals(4502877.52, result.sumOf { it.total?: BigDecimal(6678) })
    }

}