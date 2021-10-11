/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/

package com.flinty.docsflow.server.core.workspace.storage

import com.flinty.docsflow.common.core.model.domain.*
import com.gridnine.jasmine.common.core.model.BaseAsset
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.standard.model.domain.*
import com.gridnine.jasmine.server.standard.model.WorkspaceProvider

class DocsFlowWorkspaceProvider : WorkspaceProvider {
    override fun getWorkspace(): Workspace {
        val loginName = AuthUtils.getCurrentUser()
        return Storage.get().loadDocument(Workspace::class, "${loginName}_workspace")
                ?: createStandardWorkspace(loginName)
    }

    override fun saveWorkspace(workspace: Workspace):Workspace {
        val loginName = AuthUtils.getCurrentUser()
        workspace.uid = "${loginName}_workspace"
        workspace.setValue(BaseAsset.revision, -1)
        Storage.get().saveDocument(workspace)
        return workspace
    }

    private fun createStandardWorkspace(loginName: String): Workspace {
        val result = Workspace()
        result.uid = "${loginName}_workspace"
        run {
            val group = WorkspaceGroup()
            group.displayName = "Настройки"
                val item = ListWorkspaceItem()
                item.columns.add(UserAccountIndex.loginProperty.name)
                item.columns.add(UserAccountIndex.nameProperty.name)
                item.filters.add(UserAccountIndex.loginProperty.name)
                val order = SortOrder()
                order.orderType = SortOrderType.ASC
                order.field = UserAccountIndex.loginProperty.name
                item.listId = UserAccountIndex::class.qualifiedName
                item.displayName = "Профили"
                group.items.add(item)
                result.groups.add(group)
        }
        run {
            val group = WorkspaceGroup()
            group.displayName = "Поставщики"
            val item = ListWorkspaceItem()
            item.columns.add(SupplierIndex.nameProperty.name)
            val order = SortOrder()
            order.orderType = SortOrderType.ASC
            order.field = SupplierIndex.nameProperty.name
            item.displayName = "Все поставщики"
            item.listId = SupplierIndex::class.qualifiedName
            group.items.add(item)
            result.groups.add(group)
        }
        run {
            val group = WorkspaceGroup()
            group.displayName = "Проекты"
            val item = ListWorkspaceItem()
            item.columns.add(ProjectIndex.nameProperty.name)
            item.columns.add(ProjectIndex.codeProperty.name)
            val order = SortOrder()
            order.orderType = SortOrderType.ASC
            order.field = ProjectIndex.nameProperty.name
            item.displayName = "Все проекты"
            item.listId = ProjectIndex::class.qualifiedName
            group.items.add(item)
            result.groups.add(group)
        }
        run {
            val group = WorkspaceGroup()
            group.displayName = "Исходные спецификации"
            run {
                val item = ListWorkspaceItem()
                item.columns.add(SpecificationIndex.nameProperty.name)
                item.columns.add(SpecificationIndex.statusProperty.name)
                item.columns.add(SpecificationIndex.projectProperty.name)
                item.filters.add(SpecificationIndex.statusProperty.name)
                item.filters.add(SpecificationIndex.projectProperty.name)
                val order = SortOrder()
                order.orderType = SortOrderType.ASC
                order.field = SpecificationIndex.nameProperty.name
                item.displayName = "Все спецификации"
                item.listId = SpecificationIndex::class.qualifiedName
                group.items.add(item)
            }
            run {
                val item = ListWorkspaceItem()
                item.columns.add(SpecificationIndex.nameProperty.name)
                item.columns.add(SpecificationIndex.statusProperty.name)
                item.columns.add(SpecificationIndex.projectProperty.name)
                item.filters.add(SpecificationIndex.statusProperty.name)
                item.filters.add(SpecificationIndex.projectProperty.name)
                val order = SortOrder()
                order.orderType = SortOrderType.ASC
                order.field = SpecificationIndex.nameProperty.name
                item.displayName = "Новые спецификации"
                item.listId = SpecificationIndex::class.qualifiedName
                item.criterions.add(
                    SimpleWorkspaceCriterion().also {
                    it.property = SpecificationIndex.statusProperty.name
                    it.condition = WorkspaceSimpleCriterionCondition.EQUALS
                    it.value = WorkspaceSimpleCriterionEnumValues().also { crit ->
                        crit.enumClassName = SpecificationStatus::class.qualifiedName+"JS"
                        crit.values.add(SpecificationStatus.NEW.name)
                        crit.values.add(SpecificationStatus.PARTIALLY_ORDERED.name)
                    }
                })
                group.items.add(item)
            }
            result.groups.add(group)
        }
        saveWorkspace(result)
        return result
    }

}