/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.waybill.rest

import com.flinty.docsflow.common.core.model.domain.Project
import com.flinty.docsflow.common.core.model.domain.Waybill
import com.flinty.docsflow.common.core.model.rest.GenerateStoreReportRequest
import com.flinty.docsflow.common.core.model.rest.GenerateStoreReportlResponse
import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellBorderWidth
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellHorizontalAlignment
import com.gridnine.jasmine.common.standard.rest.FileDataDT
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.reports.builders.report
import com.gridnine.jasmine.server.reports.excel.ExcelGenerator
import java.math.BigDecimal
import java.util.*

class GenerateStoreReportRestHandler:RestHandler<GenerateStoreReportRequest, GenerateStoreReportlResponse> {
    override fun service(request: GenerateStoreReportRequest, ctx: RestOperationContext): GenerateStoreReportlResponse {
        val cache = hashMapOf<ObjectReference<BaseDocument>, BaseDocument >()
        val waybill = getOrLoad(cache, ObjectReference(Waybill::class, request.waybillUid, null))
        val content = ExcelGenerator.generate(report {
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
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.CENTER
                wrapText = true
            }
            list {
                columns {
                    column(10)
                    column(20)
                    column(70)
                    column(20)
                    column(10)
                    column(20)
                    column(10)
                }
                title = "Заказ"
                row()
                text("№", headerStyle)
                text("Артикул", headerStyle)
                text("Название", headerStyle)
                text("Ед. изм.", headerStyle)
                text("Склад", headerStyle)
                text("Проект", headerStyle)
                text("Кол-во", headerStyle)

                waybill.positions.filter { it.amount > BigDecimal.ZERO && it.positionNumber != null }.sortedBy { it.positionNumber }.forEach { wbPos ->
                    row()
                    val projects = linkedMapOf<Project, BigDecimal>()
                    wbPos.specificationsSplit.forEach {
                        val spec = getOrLoad(cache, it.specification)
                        val proj = getOrLoad(cache, spec.project)
                        val value = projects.getOrPut(proj){ BigDecimal.ZERO}.add(it.amount)
                        projects[proj] = value
                    }
                    val vspan = projects.size
                    number(wbPos.positionNumber?.toBigDecimal(), numberDataStyle, vSpan = vspan)
                    text(wbPos.article, textDataStyle, vSpan = vspan)
                    text(wbPos.name, textDataStyle, vSpan = vspan)
                    text(wbPos.unit.toString(), textDataStyle, vSpan = vspan)
                    number(wbPos.storeAmount, numberDataStyle, vSpan = vspan)
                    val entries = projects.entries
                    val iterator = entries.iterator()
                    for(n in 1..entries.size){
                        if(n > 1){
                            row()
                        }
                        val entry = iterator.next()
                        text(entry.key.toString(), textDataStyle)
                        number(entry.value, numberDataStyle)
                    }
                }
            }
        })
        return GenerateStoreReportlResponse().also { resp ->
            resp.result = FileDataDT().also {
                it.encodedContent = Base64.getEncoder().encodeToString(content)
                it.fileName = "waybill-${waybill.number}.xlsx"
            }
        }
    }

    private fun<D:BaseDocument> getOrLoad(cache: HashMap<ObjectReference<BaseDocument>, BaseDocument>, ref: ObjectReference<D>): D {
        return cache.getOrPut(ref as ObjectReference<BaseDocument>){
            Storage.get().loadDocument(ref)!!
        } as D
    }
}