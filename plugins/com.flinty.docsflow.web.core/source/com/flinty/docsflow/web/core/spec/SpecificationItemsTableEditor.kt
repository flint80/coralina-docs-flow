/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.spec

import com.flinty.docsflow.common.core.model.ui.SpecificationItemsTableItemEditor
import com.flinty.docsflow.common.core.model.ui.SpecificationItemsTableItemEditorVMJS
import com.flinty.docsflow.common.core.model.ui.SpecificationItemsTableItemEditorVVJS
import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class SpecificationItemsTableEditor:WebEditor<SpecificationItemsTableEditorVMJS, SpecificationItemsTableEditorVSJS, SpecificationItemsTableEditorVVJS>,BaseWebNodeWrapper<WebDataGrid<SpecificationItemsTableItemEditorVMJS>>() {
    private val localData = arrayListOf<SpecificationItemsTableItemEditorVMJS>()

    private var readOnly = false
    init {
        _node = WebUiLibraryAdapter.get().createDataGrid {
            fit = true
            fitColumns = true
            showPagination = false
            selectionType = DataGridSelectionType.MULTIPLE
            dataType = DataGridDataType.LOCAL
            column {
                fieldId = "supplier"
                title = "Поставщик"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENTITY_REFERENCE)
                width =100
            }
            column {
                fieldId = "name"
                title = "Название"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.STRING)
                width =200
            }
            column {
                fieldId = "article"
                title = "Артикул"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.STRING)
                width =100
            }
            column {
                fieldId = "amount"
                title = "Кол-во"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width =100
            }
            column {
                fieldId = "unit"
                title = "Ед. изм."
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENUM)
                width =100
            }
            column {
                fieldId = "amountNote"
                title = "Кол-во(прим)"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width =100
            }
            column {
                fieldId = "unitNote"
                title = "Ед. изм.(прим)"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.LEFT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENUM)
                width =100
            }
            column {
                fieldId = "storeAmount"
                title = "Склад"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width =100
            }
            column {
                fieldId = "toBeOrdered"
                title = "Заказ"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width =100
            }
        }
        _node.setRowDblClickListener { item->
            val editor = SpecificationItemsTableItemEditor()
            editor.readData(item, null)
            editor.setReadonly(readOnly)
            WebUiLibraryAdapter.get().showDialog(editor){
                title = "Редактор позиции"
                if(!readOnly) {
                    button {
                        displayName = "Сохранить"
                        handler = {
                            val data = editor.getData()
                            val vv = SpecificationItemsTableItemEditorVVJS()
                            val emptyMessage = "Заполните поле"
                            if (data.amount == null) {
                                vv.amount = emptyMessage
                            }
                            if (data.article == null) {
                                vv.article = emptyMessage
                            }
                            if (data.name == null) {
                                vv.name = emptyMessage
                            }
                            if (data.storeAmount == null) {
                                vv.storeAmount = emptyMessage
                            }
                            if (data.supplier == null) {
                                vv.supplier = emptyMessage
                            }
                            if (data.toBeOrdered == null) {
                                vv.toBeOrdered = emptyMessage
                            }
                            if (data.unit == null) {
                                vv.unit = emptyMessage
                            }
                            if (StandardUiUtils.hasValidationErrors(vv)) {
                                editor.showValidation(vv)
                            } else {
                                it.close()
                                item.amount = data.amount
                                item.amountNote = data.amountNote
                                item.article = data.article
                                item.name = data.name
                                item.storeAmount = data.storeAmount
                                item.supplier = data.supplier
                                item.toBeOrdered = data.toBeOrdered
                                item.unit = data.unit
                                item.unitNote = data.unitNote
                                _node.reload()
                            }
                        }
                    }
                }
                cancelButton()
            }
        }

    }

    override fun getData(): SpecificationItemsTableEditorVMJS {
        return SpecificationItemsTableEditorVMJS().also { it.items.addAll(localData) }
    }

    override fun readData(vm: SpecificationItemsTableEditorVMJS, vs: SpecificationItemsTableEditorVSJS?) {
        localData.clear()
        localData.addAll(vm.items)
        _node.setLocalData(localData)
    }

    override fun setReadonly(value: Boolean) {
        readOnly = value
    }

    override fun showValidation(vv: SpecificationItemsTableEditorVVJS?) {
        //noops
    }

    fun deleteSelectedItems(){
        val items = _node.getSelected()
        if(items.isEmpty()){
            return
        }
        val newData = localData.filter { !items.any { item -> item.article == it.article } }
        localData.clear()
        localData.addAll(newData)
        _node.setLocalData(localData)
    }

}