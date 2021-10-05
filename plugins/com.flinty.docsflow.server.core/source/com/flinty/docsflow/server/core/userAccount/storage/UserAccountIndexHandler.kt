/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.flinty.docsflow.server.core.userAccount.storage

import com.flinty.docsflow.common.core.model.domain.UserAccount
import com.flinty.docsflow.common.core.model.domain.UserAccountIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler

class UserAccountIndexHandler : IndexHandler<UserAccount, UserAccountIndex> {
    override val documentClass = UserAccount::class
    override val indexClass = UserAccountIndex::class
    override fun createIndexes(doc: UserAccount): List<UserAccountIndex> {
        val idx = UserAccountIndex()
        idx.uid = doc.uid
        idx.login = doc.login
        idx.name = doc.name
        return arrayListOf(idx)
    }
}