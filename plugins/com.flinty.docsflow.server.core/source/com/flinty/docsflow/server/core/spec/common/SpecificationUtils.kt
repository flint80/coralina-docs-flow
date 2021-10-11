/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.common

import com.flinty.docsflow.common.core.model.domain.ProjectIndex
import com.gridnine.jasmine.common.core.model.GeneralSelectBoxConfiguration
import com.gridnine.jasmine.common.core.storage.SortOrder
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.core.utils.CommonUiUtils

object SpecificationUtils {
    fun getProjectSelectConfiguration() =
        GeneralSelectBoxConfiguration().also { sb->
            val query = searchQuery {
                where { eq(ProjectIndex.activeProperty, true) }
                orderBy(ProjectIndex.nameProperty, SortOrder.ASC)
            }
            Storage.get().searchDocuments(ProjectIndex::class, query).forEach {
                sb.possibleValues.add(CommonUiUtils.toSelectItem(it.document)!!)
            }
        }
}