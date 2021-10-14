/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.invoice

import com.flinty.docsflow.common.core.model.ui.InvoicePositionsTableItemEditor
import com.flinty.docsflow.common.core.model.ui.InvoicePositionsTableItemEditorVMJS
import com.flinty.docsflow.common.core.model.ui.InvoicePositionsTableItemEditorVVJS
import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class InvoiceItemsTableEditor :
    WebEditor<InvoiceItemsTableEditorVMJS, InvoiceItemsTableEditorVSJS, InvoiceItemsTableEditorVVJS>,
    BaseWebNodeWrapper<WebDataGrid<InvoicePositionsTableItemEditorVMJS>>() {

    private val localData = arrayListOf<InvoicePositionsTableItemEditorVMJS>()

    private var readonly = false
    init {
        _node = WebUiLibraryAdapter.get().createDataGrid {
            fit = true
            fitColumns = true
            showPagination = false
            selectionType = DataGridSelectionType.NONE
            dataType = DataGridDataType.LOCAL
            column {
                fieldId = "article"
                title = "Артикул"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.STRING)
                width = 100
            }
            column {
                fieldId = "name"
                title = "Название"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.STRING)
                width = 300
            }
            column {
                fieldId = "orderAmount"
                title = "Кол-во(заказ)"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width = 60
            }
            column {
                fieldId = "unit"
                title = "Ед. изм."
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENUM)
                width =60
            }
            column {
                fieldId = "invoiceAmount"
                title = "Кол-во(счет)"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width = 60
            }
            column {
                fieldId = "totalPrice"
                title = "Полная стоимость"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width = 60
            }
            column {
                fieldId = "surplus"
                title = "Излишек"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENTITY_REFERENCE)
                width = 60
            }
        }
        _node.setRowDblClickListener { item ->
            val editor = InvoicePositionsTableItemEditor()
            editor.readData(item, null)
            editor.setReadonly(readonly)
            WebUiLibraryAdapter.get().showDialog(editor) {
                title = "Редактор позиции"
                if(!readonly){
                        button {
                            displayName = "Сохранить"
                            handler = {
                                val data = editor.getData()
                                val vv = InvoicePositionsTableItemEditorVVJS()
                                val emptyMessage = "Заполните поле"
                                if (data.invoiceAmount == null) {
                                    vv.invoiceAmount = emptyMessage
                                }
                                if (StandardUiUtils.hasValidationErrors(vv)) {
                                    editor.showValidation(vv)
                                } else {
                                    it.close()
                                    item.invoiceAmount = data.invoiceAmount
                                    item.totalPrice = data.totalPrice
                                    _node.reload()
                                }
                            }
                    }
                }
                cancelButton()
            }
        }
    }

    override fun getData(): InvoiceItemsTableEditorVMJS {
        return InvoiceItemsTableEditorVMJS().also { it.items.addAll(localData) }
    }

    override fun readData(vm: InvoiceItemsTableEditorVMJS, vs: InvoiceItemsTableEditorVSJS?) {
        localData.clear()
        localData.addAll(vm.items)
        _node.setLocalData(localData)
    }

    override fun setReadonly(value: Boolean) {
        readonly = value
    }

    override fun showValidation(vv: InvoiceItemsTableEditorVVJS?) {
        //noops
    }
}