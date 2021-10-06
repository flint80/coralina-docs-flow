/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.web.core.spec

import com.flinty.docsflow.server.core.spec.rest.SpecificationFormatJS

class ImportStandardSpecificationListButtonHandler : BaseImportSpecificationListButtonHandler() {
    override fun getSpecFormat(): SpecificationFormatJS {
        return SpecificationFormatJS.STANDARD
    }
}