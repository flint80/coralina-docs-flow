/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.web.core.order

import com.flinty.docsflow.common.core.model.ui.OrderPositionsTableItemEditor
import com.flinty.docsflow.common.core.model.ui.OrderPositionsTableItemEditorVMJS
import com.flinty.docsflow.common.core.model.ui.SpecificationItemsTableItemEditor
import com.flinty.docsflow.common.core.model.ui.SpecificationItemsTableItemEditorVMJS
import com.flinty.docsflow.web.core.spec.*
import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.WebEditor

class OrderItemsTableEditor :
    WebEditor<OrderItemsTableEditorVMJS, OrderItemsTableEditorVSJS, OrderItemsTableEditorVVJS>,
    BaseWebNodeWrapper<WebDataGrid<OrderPositionsTableItemEditorVMJS>>() {

    private val localData = arrayListOf<OrderPositionsTableItemEditorVMJS>()

    init {
        _node = WebUiLibraryAdapter.get().createDataGrid {
            fit = true
            fitColumns = true
            showPagination = false
            selectionType = DataGridSelectionType.SINGLE
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
                fieldId = "specAmount"
                title = "Кол-во(специя)"
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
                title = "Кол-во(заказ)"
                sortable = true
                horizontalAlignment = WebDataHorizontalAlignment.RIGHT
                resizable = true
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.BIG_DECIMAL)
                width = 60
            }

        }
        _node.setRowDblClickListener { item ->
            val editor = OrderPositionsTableItemEditor()
            editor.readData(item, null)
            editor.setReadonly(true)
            WebUiLibraryAdapter.get().showDialog(editor) {
                title = "Просмотр позиции"
                cancelButton()
            }
        }
    }

    fun getSelectedValue() =  _node.getSelected().let { if(it.isEmpty()) null else it[0] }

    override fun getData(): OrderItemsTableEditorVMJS {
        return OrderItemsTableEditorVMJS().also { it.items.addAll(localData) }
    }

    override fun readData(vm: OrderItemsTableEditorVMJS, vs: OrderItemsTableEditorVSJS?) {
        localData.clear()
        localData.addAll(vm.items)
        _node.setLocalData(localData)
    }

    override fun setReadonly(value: Boolean) {
        //noops
    }

    override fun showValidation(vv: OrderItemsTableEditorVVJS?) {
        //noops
    }
}