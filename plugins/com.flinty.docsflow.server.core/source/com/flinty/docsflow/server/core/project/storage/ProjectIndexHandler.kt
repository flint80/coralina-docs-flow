/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.project.storage

import com.flinty.docsflow.common.core.model.domain.Project
import com.flinty.docsflow.common.core.model.domain.ProjectIndex
import com.flinty.docsflow.common.core.model.domain.UserAccount
import com.flinty.docsflow.common.core.model.domain.UserAccountIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler

class ProjectIndexHandler : IndexHandler<Project, ProjectIndex> {
    override val documentClass = Project::class
    override val indexClass = ProjectIndex::class
    override fun createIndexes(doc: Project): List<ProjectIndex> {
        val idx = ProjectIndex()
        idx.uid = doc.uid
        idx.name = doc.name
        idx.code = doc.code
        idx.active = doc.active
        return arrayListOf(idx)
    }
}