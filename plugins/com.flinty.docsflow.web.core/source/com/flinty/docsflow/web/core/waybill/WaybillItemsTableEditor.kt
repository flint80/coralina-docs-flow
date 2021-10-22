/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.waybill

import com.flinty.docsflow.common.core.model.ui.WaybillPositionsTableItemEditor
import com.flinty.docsflow.common.core.model.ui.WaybillPositionsTableItemEditorVMJS
import com.flinty.docsflow.common.core.model.ui.WaybillPositionsTableItemEditorVVJS
import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class WaybillItemsTableEditor :
    WebEditor<WaybillItemsTableEditorVMJS, WaybillItemsTableEditorVSJS, WaybillItemsTableEditorVVJS>,
    BaseWebNodeWrapper<WebDataGrid<WaybillPositionsTableItemEditorVMJS>>() {

    private val localData = arrayListOf<WaybillPositionsTableItemEditorVMJS>()

    private var readonly = false

    init {
        _node = WebUiLibraryAdapter.get().createDataGrid {
            fit = true
            fitColumns = true
            showPagination = false
            selectionType = DataGridSelectionType.NONE
            dataType = DataGridDataType.LOCAL
            column {
                fieldId = "position"
                title = "№"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.INT)
                width = 30
            }
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
                fieldId = "invoiceAmount"
                title = "Кол-во(счет)"
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
                fieldId = "amount"
                title = "Кол-во"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width = 60
            }
        }
        _node.setRowDblClickListener { item ->
            val editor = WaybillPositionsTableItemEditor()
            editor.readData(item, null)
            editor.setReadonly(readonly)
            WebUiLibraryAdapter.get().showDialog(editor) {
                title = "Редактор позиции"
                if(!readonly){
                        button {
                            displayName = "Сохранить"
                            handler = {
                                val data = editor.getData()
                                val vv = WaybillPositionsTableItemEditorVVJS()
                                val emptyMessage = "Заполните поле"
                                if (data.amount == null) {
                                    vv.amount = emptyMessage
                                }
                                if (StandardUiUtils.hasValidationErrors(vv)) {
                                    editor.showValidation(vv)
                                } else {
                                    it.close()
                                    item.amount = data.amount
                                    item.position = data.position
                                    _node.reload()
                                }
                            }
                    }
                }
                cancelButton()
            }
        }
    }

    override fun getData(): WaybillItemsTableEditorVMJS {
        return WaybillItemsTableEditorVMJS().also { it.items.addAll(localData) }
    }

    override fun readData(vm: WaybillItemsTableEditorVMJS, vs: WaybillItemsTableEditorVSJS?) {
        localData.clear()
        localData.addAll(vm.items)
        _node.setLocalData(localData)
    }

    override fun setReadonly(value: Boolean) {
        readonly = value
    }

    override fun showValidation(vv: WaybillItemsTableEditorVVJS?) {
        //noops
    }
}