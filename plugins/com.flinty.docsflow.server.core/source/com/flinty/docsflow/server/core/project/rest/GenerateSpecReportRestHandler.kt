/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.project.rest

import com.flinty.docsflow.common.core.model.domain.*
import com.flinty.docsflow.common.core.model.rest.GenerateSpecReportRequest
import com.flinty.docsflow.common.core.model.rest.GenerateSpecReportResponse
import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.storage.SortOrder
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellBorderWidth
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellHorizontalAlignment
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportCellVerticalAlignment
import com.gridnine.jasmine.common.standard.rest.FileDataDT
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.reports.builders.report
import com.gridnine.jasmine.server.reports.excel.ExcelGenerator
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class GenerateSpecReportRestHandler:RestHandler<GenerateSpecReportRequest, GenerateSpecReportResponse> {
    override fun service(request: GenerateSpecReportRequest, ctx: RestOperationContext): GenerateSpecReportResponse {
        val cache = hashMapOf<ObjectReference<BaseDocument>, BaseDocument>()
        val projectRef = ObjectReference(Project::class, request.projectUid, null)
        val project = getOrLoad(projectRef, cache)
        val specifications = Storage.get().searchDocuments(SpecificationIndex::class, searchQuery {
            where {
                eq(SpecificationIndex.projectProperty, projectRef)
            }
            orderBy(SpecificationIndex.codeProperty, SortOrder.ASC)
        }).map { getOrLoad(it.document!!, cache) }
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
            val dateDataStyle = style {
                bottomBorderWidth = GeneratedReportCellBorderWidth.THIN
                topBorderWidth = GeneratedReportCellBorderWidth.THIN
                leftBorderWidth = GeneratedReportCellBorderWidth.THIN
                rightBorderWidth = GeneratedReportCellBorderWidth.THIN
                format = "YYYY.MM.DD"
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.RIGHT
            }
            val textDataStyle = style {
                parentStyle = numberDataStyle
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.CENTER
                wrapText = true
            }
            val supplierDataStyle = style {
                parentStyle = numberDataStyle
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.CENTER
                verticalAlignment = GeneratedReportCellVerticalAlignment.TOP
                wrapText = true
            }
            specifications.forEach { spec ->
                list {
                    row()
                    columns {
                        column(30)
                        column(70)
                        column(20)
                        column(15)
                        column(15)
                        column(15)
                        column(15)
                        column(15)
                        column(15)
                        column(15)
                        column(15)
                        column(15)
                        column(15)
                    }
                    title = spec.code
                    text("Изготовитель", headerStyle)
                    text("Название", headerStyle)
                    text("Код заказа", headerStyle)
                    text("Кол-во", headerStyle)
                    text("Ед. изм.", headerStyle)
                    text("Примечание", headerStyle, hSpan = 2)
                    emptyCell()
                    text("Склад", headerStyle)
                    text("Заказ", headerStyle)
                    text("Заказано", headerStyle)
                    text("Получено", headerStyle)
                    text("Срок", headerStyle)
                    text("Замена", headerStyle)
                    val suppliers = linkedMapOf<String, MutableList<SpecificationPosition>>()
                    spec.positions.forEach {
                        suppliers.getOrPut(it.supplier.caption!!){
                            arrayListOf()
                        }.add(it)
                    }
                    suppliers.entries.forEach {entry ->
                        entry.value.withIndex().forEach {(index, value) ->
                            row()
                            if(index == 0){
                                text(value.supplier.caption, supplierDataStyle, vSpan = entry.value.size)
                            } else {
                                emptyCell()
                            }
                            text(value.name, textDataStyle)
                            text(value.article, textDataStyle)
                            number(value.amount, numberDataStyle)
                            text(value.unit.toString(), textDataStyle)
                            number(value.amountNote, numberDataStyle)
                            text(value.unitNote?.toString(), textDataStyle)
                            number(value.storeAmount, numberDataStyle)
                            number(value.toBeOrdered, numberDataStyle)
                            val invoices = arrayListOf<Invoice>()
                            var deliveryDate:LocalDate? = null
                            if(value.order != null){
                                val order = getOrLoad(value.order!!, cache)
                                if(order.invoice != null){
                                    val invoice = getOrLoad(order.invoice!!, cache)
                                    invoices.add(invoice)
                                    deliveryDate= invoice.positions.find { it.article == value.article }?.deliveryDate
                                }
                                order.positions.find { it.article ==value.article }?.surplusSplits?.forEach { ss ->
                                    invoices.add(getOrLoad(getOrLoad(ss.surplus, cache).invoice, cache))
                                }
                            }
                            val specRef = EntityUtils.toReference(spec)
                            val invoiced = invoices.sumOf {
                                val invoicePos = it.positions.find { it.article == value.article }
                                invoicePos?.specificationSplits?.filter { it.specification == specRef }?.sumOf { it.amount }?: BigDecimal.ZERO
                            }
                            number(invoiced, numberDataStyle)
                            val waybills = invoices.flatMap { it.waybills }.map { getOrLoad(it, cache) }
                            var delivered = waybills.sumOf {
                                val waybillPos = it.positions.find { it.article == value.article }
                                waybillPos?.specificationsSplit?.filter { it.specification == specRef }?.sumOf { it.amount }?: BigDecimal.ZERO
                            }
                            number(delivered, numberDataStyle)
                            date(if(delivered >= value.toBeOrdered) null else deliveryDate, dateDataStyle)
                            text(value.replacementFor, textDataStyle)
                        }
                    }
                }
            }

        })
        return GenerateSpecReportResponse().also { resp ->
            resp.result = FileDataDT().also {
                it.encodedContent = Base64.getEncoder().encodeToString(content)
                it.fileName = "${project.code}_spec.xlsx"
            }
        }
    }

    private fun<D:BaseDocument> getOrLoad(objectReference: ObjectReference<D>, cache: HashMap<ObjectReference<BaseDocument>, BaseDocument>): D {
        return cache.getOrPut(objectReference as ObjectReference<BaseDocument>){
            Storage.get().loadDocument(objectReference)!!
        } as D
    }
}