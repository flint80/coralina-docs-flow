/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.parser.test

import com.flinty.docsflow.server.core.spec.parser.SpecialSpecificationParser
import com.flinty.docsflow.server.core.spec.parser.StandardSpecificationParser
import com.gridnine.jasmine.common.core.test.CommonCoreTestBase
import org.junit.Assert
import org.junit.Test

class SpecialSpecificationParserTest:CommonCoreTestBase() {
    @Test
    fun testStandardParser() {
        val content = SpecialSpecificationParser.convert(getContent("testdata/special.xlsx")!!)
        val items = StandardSpecificationParser.parse(content)
        Assert.assertEquals(2, items.size)
    }
}