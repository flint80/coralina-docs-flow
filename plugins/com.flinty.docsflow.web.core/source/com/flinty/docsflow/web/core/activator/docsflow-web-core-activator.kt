/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.web.core.activator

import com.flinty.docsflow.common.core.model.ui.InvoiceEditor
import com.flinty.docsflow.common.core.model.ui.OrderEditor
import com.flinty.docsflow.web.core.DomainReflectionUtilsJS
import com.flinty.docsflow.web.core.RestReflectionUtilsJS
import com.flinty.docsflow.web.core.UiReflectionUtilsJS
import com.flinty.docsflow.web.core.invoice.InvoiceEditorHandler
import com.flinty.docsflow.web.core.order.OrderEditorHandler
import com.flinty.docsflow.web.core.project.ProjectEditorHandler
import com.flinty.docsflow.web.core.spec.SpecificationEditorHandler
import com.flinty.docsflow.web.core.supplier.SupplierEditorHandler
import com.flinty.docsflow.web.core.userAccount.UserAccountEditorHandler
import com.gridnine.jasmine.common.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.SimpleActionHandler
import com.gridnine.jasmine.web.core.ui.components.WebTabsContainerTool
import com.gridnine.jasmine.web.standard.ActionsIds
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.mainframe.ActionWrapper
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.WebActionsHandler
import kotlinx.browser.window

const val pluginId = "com.flinty.docsflow.web.core"


fun main() {
    EnvironmentJS.restBaseUrl = "/ui-rest"
    RegistryJS.get().register(WebDocsFlowCoreActivator())
    if(window.asDynamic().testMode as Boolean? == true){
        return
    }
    launch {
        RegistryJS.get().allOf(ActivatorJS.TYPE).forEach { it.activate() }
        val mainFrameTools = WebActionsHandler.get().getActionsFor(ActionsIds.standard_workspace_tools).actions.map {
            it as ActionWrapper
            WebTabsContainerTool().apply {
                displayName = it.displayName
                handler = {
                    it.getActionHandler<SimpleActionHandler>().invoke()
                }
            }
        }
        val mainFrame = MainFrame {
            title = "docsflow"
            navigationWidth = 300
            tools.addAll(mainFrameTools)
        }
        EnvironmentJS.publish(mainFrame)
        WebUiLibraryAdapter.get().showWindow(mainFrame)
        val workspace = StandardRestClient.standard_standard_getWorkspace(GetWorkspaceRequestJS())
        mainFrame.setWorkspace(workspace.workspace)
        WebUiLibraryAdapter.get().showWindow(mainFrame)
    }
}

class WebDocsFlowCoreActivator : ActivatorJS {
    override suspend fun activate() {
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        DomainReflectionUtilsJS.registerWebDomainClasses()
        RestReflectionUtilsJS.registerWebRestClasses()
        UiReflectionUtilsJS.registerWebUiClasses()
        RegistryJS.get().register(UserAccountEditorHandler())
        RegistryJS.get().register(SupplierEditorHandler())
        RegistryJS.get().register(ProjectEditorHandler())
        RegistryJS.get().register(SpecificationEditorHandler())
        RegistryJS.get().register(OrderEditorHandler())
        RegistryJS.get().register(InvoiceEditorHandler())
        console.log("docsflow core module activated")
    }

    override fun getId(): String {
        return pluginId
    }

}

