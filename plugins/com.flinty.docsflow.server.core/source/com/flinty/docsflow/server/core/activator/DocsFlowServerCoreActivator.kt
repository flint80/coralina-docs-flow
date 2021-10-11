/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.activator

import com.flinty.docsflow.common.core.model.domain.Project
import com.flinty.docsflow.common.core.model.domain.UserAccount
import com.flinty.docsflow.common.core.model.domain.UserAccountIndex
import com.flinty.docsflow.server.core.project.storage.ProjectIndexHandler
import com.flinty.docsflow.server.core.project.ui.ProjectEditorHandler
import com.flinty.docsflow.server.core.spec.storage.SpecificationIndexHandler
import com.flinty.docsflow.server.core.spec.ui.SpecificationEditorHandler
import com.flinty.docsflow.server.core.supplier.storage.SupplierIndexHandler
import com.flinty.docsflow.server.core.supplier.storage.SupplierSpellVariantPropertyFindHandler
import com.flinty.docsflow.server.core.supplier.storage.SupplierSpellVariantsIndexHandler
import com.flinty.docsflow.server.core.supplier.ui.SupplierEditorHandler
import com.flinty.docsflow.server.core.userAccount.storage.UserAccountIndexHandler
import com.flinty.docsflow.server.core.userAccount.storage.UserAccountStorageInterceptor
import com.flinty.docsflow.server.core.userAccount.ui.UserAccountEditorHandler
import com.flinty.docsflow.server.core.web.DocsFlowAuthFilter
import com.flinty.docsflow.server.core.workspace.storage.DocsFlowWorkspaceProvider
import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.meta.WebPluginsAssociationsRegistry
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.utils.DigestUtils
import com.gridnine.jasmine.server.core.web.WebAppFilter
import com.gridnine.jasmine.server.core.web.WebApplication
import com.gridnine.jasmine.server.core.web.WebServerConfig
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorsRegistry
import com.gridnine.jasmine.server.standard.model.WorkspaceProvider
import com.gridnine.jasmine.server.standard.rest.ExceptionFilter
import com.gridnine.jasmine.server.standard.rest.KotlinFileDevFilter
import java.io.File
import java.util.*

class DocsFlowServerCoreActivator:IPluginActivator {
    override fun configure(config: Properties) {
        addApp("","docsflow-index","lib/docsflow-index")
        WebPluginsAssociationsRegistry.get().links["com.gridnine.jasmine.web.core"] = "/jasmine-core/com.gridnine.jasmine.web.core.js"
        WebPluginsAssociationsRegistry.get().links["com.gridnine.jasmine.web.easyui"] = "/jasmine-easyui/com.gridnine.jasmine.web.easyui.js"
        WebPluginsAssociationsRegistry.get().links["com.gridnine.jasmine.web.standard"] = "/jasmine-standard/com.gridnine.jasmine.web.standard.js"
        WebPluginsAssociationsRegistry.get().links["com.gridnine.jasmine.web.reports"] = "/jasmine-reports/com.gridnine.jasmine.web.reports.js"
        addApp("/jasmine-core","jasmine-core","lib/jasmine-core.war")
        addApp("/jasmine-easyui","jasmine-easyui","lib/jasmine-easyui.war")
        addApp("/jasmine-standard","jasmine-standard","lib/jasmine-standard.war")
        addApp("/jasmine-reports","jasmine-reports","lib/jasmine-reports.war")
        addApp("/docsflow-core","docsflow-core","lib/docsflow-core.war")
        addApp("/antd-lib","antd-lib","lib/antd-lib.war")
        addApp("/jasmine-antd","jasmine-antd","lib/jasmine-antd.war")

        WebServerConfig.get().globalFilters.add(WebAppFilter("kotlin-dev-filter", KotlinFileDevFilter::class))
        WebServerConfig.get().globalFilters.add(WebAppFilter("exception-filter", ExceptionFilter::class))
        WebServerConfig.get().globalFilters.add(WebAppFilter("auth-filter", DocsFlowAuthFilter::class))
        StorageRegistry.get().register(SupplierIndexHandler())
        StorageRegistry.get().register(SupplierSpellVariantsIndexHandler())

        StorageRegistry.get().register(UserAccountIndexHandler())
        StorageRegistry.get().register(UserAccountStorageInterceptor())
        StorageRegistry.get().register(ProjectIndexHandler())
        StorageRegistry.get().register(SpecificationIndexHandler())
        ObjectEditorsRegistry.get().register(SupplierEditorHandler())
        ObjectEditorsRegistry.get().register(UserAccountEditorHandler())
        ObjectEditorsRegistry.get().register(ProjectEditorHandler())
        ObjectEditorsRegistry.get().register(SpecificationEditorHandler())
        Environment.publish(WorkspaceProvider::class, DocsFlowWorkspaceProvider())
    }
    private fun addApp(context: String, res: String, file: String) {
        val resource = javaClass.classLoader.getResource(res)
        if(resource != null){
            WebServerConfig.get().addApplication(WebApplication(context, resource, javaClass.classLoader))
            return
        }
        val resourceFile = File(file)
        if(resourceFile.exists()){
            WebServerConfig.get().addApplication(WebApplication(context, resourceFile.toURI().toURL(), javaClass.classLoader))
        }
    }

    override fun activate(config: Properties) {
        val adminProfile = Storage.get().findUniqueDocument(UserAccountIndex::class, UserAccountIndex.loginProperty, "admin")
        if(adminProfile != null){
            return
        }
        val account = UserAccount()
        account.login = "admin"
        account.passwordDigest = DigestUtils.getMd5Hash("admin")
        account.name= "Администратор"
        Storage.get().saveDocument(account, true, "setup")
        Storage.get().saveDocument(Project().also {
            it.active = true
            it.code = "TEST"
            it.name = "Test project"
        })
    }
}