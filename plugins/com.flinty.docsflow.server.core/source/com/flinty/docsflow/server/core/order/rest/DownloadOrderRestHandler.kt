/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.order.rest

import com.flinty.docsflow.common.core.model.domain.SpecificationOrder
import com.flinty.docsflow.common.core.model.rest.DownloadOrderRequest
import com.flinty.docsflow.common.core.model.rest.DownloadOrderResponse
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

class DownloadOrderRestHandler:RestHandler<DownloadOrderRequest, DownloadOrderResponse> {
    override fun service(request: DownloadOrderRequest, ctx: RestOperationContext): DownloadOrderResponse {
        val order = Storage.get().loadDocument(SpecificationOrder::class, request.orderUid)!!
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
                    column(70)
                    column(20)
                    column(20)
                    column(10)
                }
                title = "Заказ"
                row()
                text("Название", headerStyle)
                text("Артикул", headerStyle)
                text("Количество", headerStyle)
                text("Ед. изм.", headerStyle)
                order.positions.filter { BigDecimal.ZERO.compareTo(it.amount) != 0 }.forEach {
                    row()
                    text(it.name, textDataStyle)
                    text(it.article, textDataStyle)
                    number(it.amount, numberDataStyle)
                    text(it.unit.toString(), textDataStyle)
                }
            }
        })
        return DownloadOrderResponse().also {resp ->
            resp.file = FileDataDT().also {
                it.encodedContent = Base64.getEncoder().encodeToString(content)
                it.fileName = "${order.supplier}_order.xlsx"
            }
        }
    }
}