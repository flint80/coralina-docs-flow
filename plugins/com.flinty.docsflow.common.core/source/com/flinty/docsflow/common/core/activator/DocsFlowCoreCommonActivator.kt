package com.flinty.docsflow.common.core.activator

import com.flinty.docsflow.common.core.WebPluginsAssociations
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import com.gridnine.jasmine.common.core.parser.RestMetadataParser
import com.gridnine.jasmine.common.core.parser.UiMetadataParser
import java.util.*

class DocsFlowCoreCommonActivator:IPluginActivator {
    override fun configure(config: Properties) {
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-user-account-domain.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-user-account-rest.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-user-account-ui.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-supplier-domain.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-supplier-rest.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-supplier-ui.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-project-domain.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-project-rest.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-project-ui.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-spec-domain.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-spec-ui.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-spec-rest.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-order-domain.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-order-ui.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-order-rest.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-invoice-domain.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-invoice-rest.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-invoice-ui.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/flinty/docsflow/common/core/model/docsflow-surplus-domain.xml", javaClass.classLoader)

        WebPluginsAssociations.registerAssociations()
    }
}