/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/

package com.flinty.docsflow.server.core.supplier.storage

import com.flinty.docsflow.common.core.model.domain.Supplier
import com.flinty.docsflow.common.core.model.domain.SupplierSpellVariantIndex
import com.gridnine.jasmine.server.core.storage.cache.CacheConfiguration
import kotlin.reflect.KClass

class SupplierSpellVariantPropertyFindHandler: CacheConfiguration.CachedPropertyHandler<Supplier>{
    override fun getIndexClass(): KClass<*> {
        return SupplierSpellVariantIndex::class
    }

    override fun getPropertyName(): String {
        return SupplierSpellVariantIndex.spellVariantProperty.name
    }

    override fun getIdentityClass(): KClass<Supplier> {
        return Supplier::class
    }

    override fun getValue(obj: Supplier): Any? {
        return obj.name
    }
}