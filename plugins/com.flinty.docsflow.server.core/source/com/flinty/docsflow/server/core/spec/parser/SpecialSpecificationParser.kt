/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.parser

import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellBorderWidth
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellHorizontalAlignment
import com.gridnine.jasmine.server.reports.builders.report
import com.gridnine.jasmine.server.reports.excel.ExcelGenerator
import com.gridnine.jasmine.server.reports.excel.ExcelUtils
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigDecimal

object SpecialSpecificationParser{
    fun convert(content:ByteArray):ByteArray{
        val workbook = ByteArrayInputStream(content).use {
            XSSFWorkbook(it)
        }
        val it = workbook.sheetIterator()
        it.next()
        val specSheet = it.next()
        val result = arrayListOf<ParsedSpecificationPosition>()
        var sectionStarted = false
        var lastPosition: ParsedSpecificationPosition? = null
        var sectionJustStarted = false
        var lastSupplier:String? = null
        for(idx in 0..specSheet.lastRowNum){
            val toBeOrdered = ExcelUtils.getNumberValue(specSheet, idx, ExcelUtils.getCellIndex("N"), 5,true)
            if(toBeOrdered != null){
                sectionJustStarted = true
                sectionStarted = true
                lastPosition = ParsedSpecificationPosition().also {
                    it.toBeOrdered = toBeOrdered
                    it.storeAmount = BigDecimal.ZERO
                    it.amount = toBeOrdered
                    it.unit = ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("M"), true)!!.let {
                        SpecParcerUtils.getUnit(it)
                    }
                    it.supplier = ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("L"), false)?:lastSupplier!!
                    lastSupplier= it.supplier
                    it.name = ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("C"), true)!!
                }
                result.add(lastPosition)
            }
            if(sectionStarted){
                val article = ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("H"), false)
                if(TextUtils.isBlank(article)){
                    sectionStarted = false
                } else {
                    lastPosition!!.article = if(sectionJustStarted) article!! else "${lastPosition.article} $article"
                }
                sectionJustStarted = false
            }
        }
        return ExcelGenerator.generate(report {
            fileName = "test.xlsx"
            val headerStyle = style {
                fontBold = true
                bottomBorderWidth = GeneratedReportCellBorderWidth.THICK
                topBorderWidth = GeneratedReportCellBorderWidth.THICK
                leftBorderWidth = GeneratedReportCellBorderWidth.THICK
                rightBorderWidth = GeneratedReportCellBorderWidth.THICK
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.CENTER
            }
            val numberDataStyle = style {
                bottomBorderWidth = GeneratedReportCellBorderWidth.THIN
                topBorderWidth = GeneratedReportCellBorderWidth.THIN
                leftBorderWidth = GeneratedReportCellBorderWidth.THIN
                rightBorderWidth = GeneratedReportCellBorderWidth.THIN
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.RIGHT
            }
            val textDataStyle = style {
                parentStyle = numberDataStyle
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.LEFT
            }
            list {
                title = "Спецификация"
                columns {
                    column(20)
                    column(40)
                    column(40)
                    column(10)
                    column(10)
                    column(10)
                    column(10)
                    column(10)
                    column(10)
                }
                row()
                text("Изготовитель", headerStyle)
                text("Наименование", headerStyle)
                text("Код заказа", headerStyle)
                text("Кол-во", headerStyle)
                text("Ед. изм.", headerStyle)
                text("Кол-во(прим)", headerStyle)
                text("Ед. изм.(прим)", headerStyle)
                text("Склад", headerStyle)
                text("Заказ", headerStyle)
                result.forEach {
                    row()
                    text(it.supplier, textDataStyle)
                    text(it.name, textDataStyle)
                    text(it.article, textDataStyle)
                    number(it.amount, numberDataStyle)
                    text(it.unit.toString(), textDataStyle)
                    emptyCell(numberDataStyle)
                    emptyCell(textDataStyle)
                    number(it.storeAmount, numberDataStyle)
                    number(it.toBeOrdered, numberDataStyle)
                }
            }
        }
        )
    }
}