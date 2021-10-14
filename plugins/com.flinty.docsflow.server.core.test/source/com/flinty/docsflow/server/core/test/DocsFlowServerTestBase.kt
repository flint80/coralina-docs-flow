/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.test

import com.flinty.docsflow.common.core.model.domain.Project
import com.flinty.docsflow.common.core.model.domain.Supplier
import com.flinty.docsflow.common.core.model.domain.SupplierType
import com.flinty.docsflow.server.core.invoice.storage.InvoiceIndexHandler
import com.flinty.docsflow.server.core.invoice.storage.InvoiceInterceptor
import com.flinty.docsflow.server.core.order.storage.OrderIndexHandler
import com.flinty.docsflow.server.core.order.storage.OrderInterceptor
import com.flinty.docsflow.server.core.project.storage.ProjectIndexHandler
import com.flinty.docsflow.server.core.spec.storage.SpecificationIndexHandler
import com.flinty.docsflow.server.core.spec.storage.SpecificationInterceptor
import com.flinty.docsflow.server.core.supplier.storage.SupplierIndexHandler
import com.flinty.docsflow.server.core.supplier.storage.SupplierSpellVariantsIndexHandler
import com.flinty.docsflow.server.core.userAccount.storage.UserAccountIndexHandler
import com.flinty.docsflow.server.core.userAccount.storage.UserAccountStorageInterceptor
import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import com.gridnine.jasmine.common.core.parser.RestMetadataParser
import com.gridnine.jasmine.common.core.parser.UiMetadataParser
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.test.storage.StorageTestBase
import com.gridnine.jasmine.server.standard.model.SequenceNumberGenerator

open class DocsFlowServerTestBase:StorageTestBase() {

    private var projectIndex = 1

    override fun setUp() {
        super.setUp()
        Environment.publish(SequenceNumberGenerator())
    }
    override fun configureStorageRegistry() {
        StorageRegistry.get().register(SupplierIndexHandler())
        StorageRegistry.get().register(SupplierSpellVariantsIndexHandler())

        StorageRegistry.get().register(UserAccountIndexHandler())
        StorageRegistry.get().register(UserAccountStorageInterceptor())
        StorageRegistry.get().register(ProjectIndexHandler())
        StorageRegistry.get().register(SpecificationIndexHandler())
        StorageRegistry.get().register(OrderIndexHandler())

        StorageRegistry.get().register(InvoiceIndexHandler())

        StorageRegistry.get().register(SpecificationInterceptor())
        StorageRegistry.get().register(OrderInterceptor())
        StorageRegistry.get().register(InvoiceInterceptor())

    }

    override fun registerDomainMetadata(result: DomainMetaRegistry) {
        super.registerDomainMetadata(result)
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/gridnine/jasmine/common/standard/model/standard-model-domain.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/flinty/docsflow/common/core/model/docsflow-user-account-domain.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/flinty/docsflow/common/core/model/docsflow-supplier-domain.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/flinty/docsflow/common/core/model/docsflow-project-domain.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/flinty/docsflow/common/core/model/docsflow-spec-domain.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/flinty/docsflow/common/core/model/docsflow-order-domain.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/flinty/docsflow/common/core/model/docsflow-invoice-domain.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/flinty/docsflow/common/core/model/docsflow-surplus-domain.xml", javaClass.classLoader)



    }
    protected fun createSupplier(name:String):ObjectReference<Supplier>{
        val supplier = Supplier().also {
            it.type = SupplierType.STANDARD
            it.spellVariants.add(name.toLowerCase())
            it.name = name
        }
        Storage.get().saveDocument(supplier)
        return EntityUtils.toReference(supplier)
    }

    protected fun createProject(name:String, code:String? = null):ObjectReference<Project>{
        val project = Project().also {
            it.active = true
            it.name = name
            it.code = code?:"P${projectIndex}"
            projectIndex++
            it.name = name
        }
        Storage.get().saveDocument(project)
        return EntityUtils.toReference(project)
    }
}