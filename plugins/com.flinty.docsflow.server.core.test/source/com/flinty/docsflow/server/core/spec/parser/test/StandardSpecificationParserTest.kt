/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.parser.test

import com.flinty.docsflow.server.core.spec.parser.StandardSpecificationParser
import com.gridnine.jasmine.common.core.test.CommonCoreTestBase
import org.junit.Test

class StandardSpecificationParserTest:CommonCoreTestBase() {
    @Test
    fun testStandardParser() {
        val content = getContent("testdata/standard.xlsx")!!
        val result = StandardSpecificationParser.parse(content)

    }
}