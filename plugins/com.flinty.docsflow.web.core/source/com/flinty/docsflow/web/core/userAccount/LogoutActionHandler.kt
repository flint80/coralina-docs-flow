/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.flinty.docsflow.web.core.userAccount

import com.flinty.docsflow.common.core.model.rest.LogoutRequestJS
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.web.core.ui.components.SimpleActionHandler
import kotlinx.browser.window

class LogoutActionHandler:SimpleActionHandler {
    override suspend fun invoke() {
        DocsflowRestClient.docsflow_userAccount_logout(LogoutRequestJS())
        window.location.href = "/login.html"
    }
}