/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.invoice.rest

import com.flinty.docsflow.common.core.model.rest.BasicInvoiceParsedItem
import com.flinty.docsflow.common.core.model.rest.ParseSchneiderInvoiceRequest
import com.flinty.docsflow.common.core.model.rest.ParseSchneiderInvoiceResponse
import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import java.math.BigDecimal

class ParseSchneiderInvoiceRestHandler:RestHandler<ParseSchneiderInvoiceRequest, ParseSchneiderInvoiceResponse> {
    override fun service(
        request: ParseSchneiderInvoiceRequest,
        ctx: RestOperationContext
    ): ParseSchneiderInvoiceResponse {
        return ParseSchneiderInvoiceResponse().also {
            it.items.addAll(parseFile(request.content))
        }
    }

    fun parseFile(content: String): List<BasicInvoiceParsedItem> {
        val result = arrayListOf<BasicInvoiceParsedItem>()
        val subContent = content.substringBeforeLast("Коммерческое предложение")
        val startRegexp = Regex("([0-9]{1,3}0)\\s+(\\S{2,30})")
        val amountRegexp = Regex("([0-9,.]{1,4})\\s+(ШТ)")
        val priceRegexp = Regex("([0-9,.]+)\\s+(РУБ)")
        var lastItem: BasicInvoiceParsedItem? = null
        var lastSectionStartedIndex = -10
//        val notProcessed = ArrayList(subContent.lines())
        subContent.lines().withIndex().forEach {(index, line) ->
            var correctedLine = line.trim()
            var res = startRegexp.find(correctedLine)
            if(res != null && correctedLine.startsWith(res.value)){
//                notProcessed.remove(line)
                lastSectionStartedIndex = index
                val article = res.groupValues[2]
                lastItem = BasicInvoiceParsedItem().also {
                    it.article = article
                }
                result.add(lastItem!!)
                correctedLine = correctedLine.substring(correctedLine.length-10)
                res = amountRegexp.find(correctedLine)
                if(res == null){
                    throw Xeption.forEndUser(L10nMessage("не удалось определить количество для позиции $article"))
                }
                lastItem!!.amount = toBigDecimal(res.groupValues[1])
            } else if(index - lastSectionStartedIndex <= 2){
                res = priceRegexp.find(correctedLine)
                if(res!= null){
                    lastItem!!.total = toBigDecimal(res.groupValues[1])
                }
            }
        }
//        notProcessed.forEach {
//            println(it)
//        }
        return result
    }

    private fun toBigDecimal(s: String): BigDecimal {
        return s.replace(".","").replace(",",".").toBigDecimal()
    }
}