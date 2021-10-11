/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.spec

import com.flinty.docsflow.common.core.model.rest.ConvertSpecialSpecificationRequestJS
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.web.core.utils.ContentTypeJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.list.ListLinkButtonHandler
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class ConvertSpecialSpecificationListButtonHandler: ListLinkButtonHandler<BaseIdentityJS> {
    override suspend fun invoke(selected: List<BaseIdentityJS>) {
        val data = MiscUtilsJS.uploadFile() ?: return
        val content = DocsflowRestClient.docsflow_specification_convertSpecialSpecification(ConvertSpecialSpecificationRequestJS().also {
            it.content = data.content
        }).content
        MiscUtilsJS.downloadFile("${data.fileName.substringBeforeLast(".")}_corrected.xlsx", ContentTypeJS.EXCEL, content)
    }
}