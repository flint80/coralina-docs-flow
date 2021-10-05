/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/

package com.flinty.docsflow.server.core.userAccount.storage

import com.flinty.docsflow.common.core.model.domain.UserAccount
import com.flinty.docsflow.common.core.model.domain.UserAccountIndex
import com.gridnine.jasmine.server.core.storage.cache.CacheConfiguration
import kotlin.reflect.KClass

class UserAccountLoginPropertyFindHandler: CacheConfiguration.CachedPropertyHandler<UserAccount>{
    override fun getIndexClass(): KClass<*> {
        return UserAccountIndex::class
    }

    override fun getPropertyName(): String {
        return UserAccountIndex.loginProperty.name
    }

    override fun getIdentityClass(): KClass<UserAccount> {
        return UserAccount::class
    }

    override fun getValue(obj: UserAccount): Any? {
        return obj.login
    }
}