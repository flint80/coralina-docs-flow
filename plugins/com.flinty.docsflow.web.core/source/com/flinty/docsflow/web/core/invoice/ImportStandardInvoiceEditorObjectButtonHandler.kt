/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.invoice

import com.flinty.docsflow.common.core.model.domain.StandardInvoiceColumnTypeJS
import com.flinty.docsflow.common.core.model.rest.ParseStandardExcelRequestJS
import com.flinty.docsflow.common.core.model.ui.InvoiceEditor
import com.flinty.docsflow.web.core.DocsflowRestClient
import com.gridnine.jasmine.common.core.model.BaseVMJS
import com.gridnine.jasmine.common.core.model.BaseVSJS
import com.gridnine.jasmine.common.core.model.BaseVVJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebTag
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import com.gridnine.jasmine.web.standard.widgets.*
import kotlinx.browser.window

class ImportStandardInvoiceEditorObjectButtonHandler:
    ObjectEditorTool<InvoiceEditor> {
    override suspend fun invoke(editor: ObjectEditor<InvoiceEditor>) {
        var actual = true
        val dialogEditor  =  StandardInvoiceParseDialog()
        window.navigator.clipboard.readText().then { value ->
            if(actual){
               launch {
                   val result = DocsflowRestClient.docsflow_invoice_parseStandardInvoice(ParseStandardExcelRequestJS().also {
                       it.content = value
                   })
                   val vm = StandardInvoiceParseDialogVM().also {
                       it.items.addAll(result.items.map { it.fields })
                       it.columns.addAll(it.items[0].map { StandardInvoiceColumnTypeJS.IGNORE })
                   }
                   dialogEditor.readData(vm, null)
               }
            }
        }
        WebUiLibraryAdapter.get().showDialog(dialogEditor){
            title = "Импорт счета"
            button {
                displayName = "Импортировать"
                handler = {
                    actual = false
                    val data = dialogEditor.getData()
                    if(!data.columns.any { it == StandardInvoiceColumnTypeJS.ARTICLE } || !data.columns.any { it == StandardInvoiceColumnTypeJS.AMOUNT }){
                        StandardUiUtils.showError("Должны быть выбраны колонки Артикул и Кол-во")
                    } else {
                        val currentData = editor.getEditor().positionsWidget.getData()
                        val articleIndex = data.columns.indexOf(StandardInvoiceColumnTypeJS.ARTICLE)
                        val amountIndex = data.columns.indexOf(StandardInvoiceColumnTypeJS.AMOUNT)
                        val priceIndex = data.columns.indexOf(StandardInvoiceColumnTypeJS.TOTAL_PRICE)
                        var hasError = false
                        data.items.forEach { item ->
                            val article = item[articleIndex]
                            val position = currentData.items.find { article.contains(it.article!!)}
                            if(position == null){
//                                hasError = true
//                                StandardUiUtils.showError("В счете отсутствует позиция с артикулом \"${article}\"")
                                return@forEach
                            }
                            val amountStr = item[amountIndex]
                            val amount = amountStr.replace(" ","").replace(",",".").toDouble()
                            position.invoiceAmount = amount
                            if(priceIndex != -1){
                                val priceStr = item[priceIndex]
                                val price = priceStr.replace(" ","").replace(",",".").toDouble()
                                position.totalPrice = price
                            }
                        }
                        if(!hasError) {
                            it.close()
                            editor.getEditor().positionsWidget.readData(currentData, null)
                        }
                    }
                }
            }
            button {
                displayName = "Нет"
                handler = {
                    actual = false
                    it.close()
                }
            }
        }
    }
}

class StandardInvoiceParseDialogVS:BaseVSJS()
class StandardInvoiceParseDialogVV:BaseVVJS()
class StandardInvoiceParseDialogVM:BaseVMJS(){
    val items = arrayListOf<MutableList<String>>()
    val columns = arrayListOf<StandardInvoiceColumnTypeJS>()
}

class StandardInvoiceParseDialog : WebEditor<StandardInvoiceParseDialogVM, StandardInvoiceParseDialogVS, StandardInvoiceParseDialogVV>,BaseWebNodeWrapper<WebTag>(){
    private lateinit var vm:StandardInvoiceParseDialogVM
    private var columnTypes = arrayListOf<EnumValueWidget<StandardInvoiceColumnTypeJS>>()
    init {
        _node = WebUiLibraryAdapter.get().createTag("div", "invoiceParseDialog${MiscUtilsJS.createUUID()}")
        _node.getStyle().setParameters("width" to "800px", "height" to "500px", "overflow-y" to "auto")
    }
    override fun getData(): StandardInvoiceParseDialogVM {
        columnTypes.withIndex().forEach { vm.columns[it.index] = it.value.getValue()!!}
        return vm
    }

    override fun readData(vm: StandardInvoiceParseDialogVM, vs: StandardInvoiceParseDialogVS?) {
        val table = GeneralTableBoxWidget{
            width = "100%"
            height = "100%"
            vm.columns.forEach {
                columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(null, 150, null))
                val enum = EnumValueWidget<StandardInvoiceColumnTypeJS>{
                    width = "100%"
                    allowNull = false
                    enumClass = StandardInvoiceColumnTypeJS::class
                }
                enum.setValue(StandardInvoiceColumnTypeJS.IGNORE)
                columnTypes.add(enum)
                headerComponents.add(enum)
            }
        }
        vm.items.forEach {
            val components = arrayListOf<WebGeneralTableBoxWidgetCell>()
            it.forEach { item ->
                components.add(WebGeneralTableBoxWidgetCell(WebLabelWidget(item)))
            }
            table.addRow(null, components)
        }
        _node.getChildren().addChild(table)
        this.vm = vm
    }

    override fun setReadonly(value: Boolean) {
        //noops
    }

    override fun showValidation(vv: StandardInvoiceParseDialogVV?) {
        //noops
    }

}