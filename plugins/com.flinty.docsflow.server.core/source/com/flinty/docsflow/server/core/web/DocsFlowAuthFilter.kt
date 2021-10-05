/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.web

import com.flinty.docsflow.common.core.model.domain.UserAccountIndex
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.server.core.utils.DESUtils
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class DocsFlowAuthFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpReq = request as HttpServletRequest
        var login: String? = null
        if(httpReq.requestURI == "/login.html" || httpReq.requestURI == "/ui-rest/docsflow_userAccount_login"){
            chain.doFilter(request, response)
            return
        }
        val authCookie = httpReq.cookies?.find { it.name == DOCSFLOW_AUTH_COOKIE }?.value
        if (authCookie == null || !checkAuthCookie(authCookie) { login = it }) {
            val httpResp = response as HttpServletResponse
            httpResp.status = HttpServletResponse.SC_UNAUTHORIZED
            httpResp.sendRedirect("/login.html")
            return
        }
        AuthUtils.setCurrentUser(login!!)
        try{
            chain.doFilter(request, response)
        }finally {
            AuthUtils.resetCurrentUser()
        }
    }

    private fun checkAuthCookie(authCookie: String, loginSetter:(String)->Unit): Boolean {
        try {
            val authString = DESUtils.decrypt(authCookie)
            val login = authString.substringBefore("|")
            loginSetter.invoke(login)
            val passwordDigest = authString.substringAfter("|")
            val user = Storage.get().findUniqueDocument(UserAccountIndex::class, UserAccountIndex.loginProperty, login)
                ?: return false
            return user.passwordDigest?.equals(passwordDigest) ?: false
        } catch (e: Throwable) {
            return false
        }
    }

    companion object {
        const val DOCSFLOW_AUTH_COOKIE = "docsflow-auth-cookie"
    }
}