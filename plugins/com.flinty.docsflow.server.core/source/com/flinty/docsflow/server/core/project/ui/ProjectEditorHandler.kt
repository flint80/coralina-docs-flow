/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.flinty.docsflow.server.core.project.ui

import com.flinty.docsflow.common.core.model.domain.Project
import com.flinty.docsflow.common.core.model.ui.ProjectEditorVM
import com.flinty.docsflow.common.core.model.ui.ProjectEditorVS
import com.flinty.docsflow.common.core.model.ui.ProjectEditorVV
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import kotlin.reflect.KClass

class ProjectEditorHandler:ObjectEditorHandler<Project, ProjectEditorVM, ProjectEditorVS, ProjectEditorVV>{
    override fun getObjectClass(): KClass<Project> {
        return Project::class
    }

    override fun getVMClass(): KClass<ProjectEditorVM> {
        return ProjectEditorVM::class
    }

    override fun getVSClass(): KClass<ProjectEditorVS> {
        return ProjectEditorVS::class
    }

    override fun getVVClass(): KClass<ProjectEditorVV> {
        return ProjectEditorVV::class
    }

    override fun fillSettings(entity: Project, vsEntity: ProjectEditorVS, vmEntity: ProjectEditorVM, ctx: MutableMap<String, Any?>) {
    }
    override fun read(entity: Project, vmEntity: ProjectEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.name = entity.name
        vmEntity.code = entity.code
        vmEntity.active = entity.active
    }

    override fun getTitle(entity: Project, vmEntity: ProjectEditorVM, vsEntity: ProjectEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.code
    }

    override fun write(entity: Project, vmEntity: ProjectEditorVM, ctx: MutableMap<String, Any?>) {
        entity.name = vmEntity.name
        entity.code = vmEntity.code!!
        entity.active = vmEntity.active
    }

    override fun validate(vmEntity: ProjectEditorVM, vvEntity: ProjectEditorVV, ctx: MutableMap<String, Any?>) {
        if(TextUtils.isBlank(vmEntity.name)){
            vvEntity.name = StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(vmEntity.code)){
            vvEntity.code = StandardL10nMessagesFactory.Empty_field()
        }
    }
}