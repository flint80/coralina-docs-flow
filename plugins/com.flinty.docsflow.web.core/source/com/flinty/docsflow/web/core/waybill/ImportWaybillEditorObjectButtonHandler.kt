/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.waybill

import com.flinty.docsflow.common.core.model.domain.StandardInvoiceColumnTypeJS
import com.flinty.docsflow.common.core.model.domain.WaybillColumnTypeJS
import com.flinty.docsflow.common.core.model.rest.ParseStandardExcelRequestJS
import com.flinty.docsflow.common.core.model.ui.WaybillEditor
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

class ImportWaybillEditorObjectButtonHandler : ObjectEditorTool<WaybillEditor> {
    override suspend fun invoke(editor: ObjectEditor<WaybillEditor>) {
            var actual = true
            val dialogEditor  =  WaybillParseDialog()
            window.navigator.clipboard.readText().then { value ->
                if(actual){
                    launch {
                        val result = DocsflowRestClient.docsflow_invoice_parseStandardInvoice(
                            ParseStandardExcelRequestJS().also {
                            it.content = value
                        })
                        val vm = WaybillParseDialogVM().also {
                            it.items.addAll(result.items.map { it.fields })
                            it.columns.addAll(it.items[0].map { WaybillColumnTypeJS.IGNORE })
                        }
                        dialogEditor.readData(vm, null)
                    }
                }
            }
            WebUiLibraryAdapter.get().showDialog(dialogEditor){
                title = "Импорт накладной"
                button {
                    displayName = "Импортировать"
                    handler = {
                        actual = false
                        val data = dialogEditor.getData()
                        if(!data.columns.any { it == WaybillColumnTypeJS.ARTICLE } || !data.columns.any { it == WaybillColumnTypeJS.AMOUNT }){
                            StandardUiUtils.showError("Должны быть выбраны колонки Артикул и Кол-во")
                        } else {
                            val currentData = editor.getEditor().positionsWidget.getData()
                            val articleIndex = data.columns.indexOf(WaybillColumnTypeJS.ARTICLE)
                            val amountIndex = data.columns.indexOf(WaybillColumnTypeJS.AMOUNT)
                            val positionIndex = data.columns.indexOf(WaybillColumnTypeJS.POSITION)
                            var hasError = false
                            data.items.forEach { item ->
                                val article = item[articleIndex]
                                val position = currentData.items.find { article.contains(it.article!!)}
                                if(position != null) {
                                    val amountStr = item[amountIndex]
                                    val amount = amountStr.replace(" ", "").replace(",", ".").toDouble()
                                    position.amount = amount
                                    if(positionIndex != -1){
                                        position.position = item[positionIndex].trim().toInt()
                                    }
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

    class WaybillParseDialogVS: BaseVSJS()
    class WaybillParseDialogVV: BaseVVJS()
    class WaybillParseDialogVM: BaseVMJS(){
        val items = arrayListOf<MutableList<String>>()
        val columns = arrayListOf<WaybillColumnTypeJS>()
    }

    class WaybillParseDialog : WebEditor<WaybillParseDialogVM, WaybillParseDialogVS, WaybillParseDialogVV>,
        BaseWebNodeWrapper<WebTag>(){
        private lateinit var vm:WaybillParseDialogVM
        private var columnTypes = arrayListOf<EnumValueWidget<WaybillColumnTypeJS>>()
        init {
            _node = WebUiLibraryAdapter.get().createTag("div", "waybillParseDialog${MiscUtilsJS.createUUID()}")
            _node.getStyle().setParameters("width" to "800px", "height" to "500px", "overflow-y" to "auto")
        }
        override fun getData(): WaybillParseDialogVM {
            columnTypes.withIndex().forEach { vm.columns[it.index] = it.value.getValue()!!}
            return vm
        }

        override fun readData(vm: WaybillParseDialogVM, vs: WaybillParseDialogVS?) {
            val table = GeneralTableBoxWidget{
                width = "100%"
                height = "100%"
                vm.columns.forEach {
                    columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(null, 150, null))
                    val enum = EnumValueWidget<WaybillColumnTypeJS>{
                        width = "100%"
                        allowNull = false
                        enumClass = WaybillColumnTypeJS::class
                    }
                    enum.setValue(WaybillColumnTypeJS.IGNORE)
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

        override fun showValidation(vv: WaybillParseDialogVV?) {
            //noops
        }

    }