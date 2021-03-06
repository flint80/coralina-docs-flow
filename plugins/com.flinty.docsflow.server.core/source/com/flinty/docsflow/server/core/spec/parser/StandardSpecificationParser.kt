/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.parser

import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.server.reports.excel.ExcelUtils
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayInputStream

object StandardSpecificationParser{
    fun parse(content:ByteArray):List<ParsedSpecificationPosition>{
        val workbook = ByteArrayInputStream(content).use {
            XSSFWorkbook(it)
        }
        var specSheet:Sheet? = null
        for(sheet in workbook.sheetIterator()){
            if(specSheet == null || sheet.sheetName.toLowerCase().contains("спецификация")){
                specSheet = sheet
            }
        }
        if(specSheet == null){
            throw Xeption.forEndUser(L10nMessage("в файле отсутствует страница Спецификация"))
        }
        var lastSupplier:String? = null
        val result = arrayListOf<ParsedSpecificationPosition>()
        var sectionStarted = false
        val articles = hashSetOf<String>()
        for(idx in 0..specSheet.lastRowNum){
            val toBeOrdered = ExcelUtils.getNumberValue(specSheet, idx, ExcelUtils.getCellIndex("I"), 5,true)
            val supplier = ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("A"), false)?:lastSupplier
            lastSupplier = supplier
            val name = ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("B"), false)
            val article = ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("C"), false)
            if(article != null && !articles.add(article!!)){
                throw Xeption.forEndUser(L10nMessage("найдено несколько позиций с артикулом $article"))
            }
            val amount = ExcelUtils.getNumberValue(specSheet, idx, ExcelUtils.getCellIndex("D"), 5,true)
            val unit = if(toBeOrdered!= null) ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("E"), false)?.let {
                SpecParcerUtils.getUnit(it)
            } else null
            val primAmount = ExcelUtils.getNumberValue(specSheet, idx, ExcelUtils.getCellIndex("F"), 5,true)
            val primUnit = ExcelUtils.getStringValue(specSheet, idx, ExcelUtils.getCellIndex("G"), false)?.let { SpecParcerUtils.getUnit(it) }
            val storeAmount = ExcelUtils.getNumberValue(specSheet, idx, ExcelUtils.getCellIndex("H"), 5,true)
            if(toBeOrdered != null){
                sectionStarted = true
                if(TextUtils.isBlank(name)){
                    throw Xeption.forEndUser(L10nMessage("в позиции ${idx+1} указана сумма заказа но не указано наименование"))
                }
                if(TextUtils.isBlank(supplier)){
                    throw Xeption.forEndUser(L10nMessage("в позиции ${idx+1} указана сумма заказа но не указан поставщик"))
                }
                if(TextUtils.isBlank(article)){
                    throw Xeption.forEndUser(L10nMessage("в позиции ${idx+1} указана сумма заказа но не указан артикул"))
                }
                if(amount == null){
                    throw Xeption.forEndUser(L10nMessage("в позиции ${idx+1} указана сумма заказа но не указано количество"))
                }
                if(unit == null){
                    throw Xeption.forEndUser(L10nMessage("в позиции ${idx+1} указана сумма заказа но не указана единица измерения"))
                }
                if(storeAmount == null){
                    throw Xeption.forEndUser(L10nMessage("в позиции ${idx+1} указана сумма заказа но не указано количество на складе"))
                }
                result.add(ParsedSpecificationPosition().also {
                    it.amount = amount!!
                    it.amountNote = primAmount
                    it.article = article!!
                    it.name = name!!
                    it.storeAmount = storeAmount
                    it.supplier = supplier!!
                    it.toBeOrdered = toBeOrdered
                    it.unit = unit
                    it.unitNote = primUnit
                })
            } else if (article != null && sectionStarted){
                throw Xeption.forEndUser(L10nMessage("в позиции ${idx+1} указан артикул но не указана сумма заказа"))
            }
        }
        return result
    }
}