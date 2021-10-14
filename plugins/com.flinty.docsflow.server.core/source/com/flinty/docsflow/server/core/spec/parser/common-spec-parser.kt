/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.parser

import com.flinty.docsflow.common.core.model.domain.PositionUnit
import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption

class ParsedSpecificationPosition {

    lateinit var article: String

    lateinit var name: String

    lateinit var supplier: String

    lateinit var amount: java.math.BigDecimal

    lateinit var unit: com.flinty.docsflow.common.core.model.domain.PositionUnit

    var amountNote: java.math.BigDecimal? = null

    var unitNote: com.flinty.docsflow.common.core.model.domain.PositionUnit? = null

    lateinit var storeAmount: java.math.BigDecimal

    lateinit var toBeOrdered: java.math.BigDecimal
}

object SpecParcerUtils{
    fun getUnit(strValue:String):PositionUnit{
        val lc = strValue.toLowerCase()
        return when{
            lc.contains("шт") ->PositionUnit.PIECE
            lc.contains("уп") ->PositionUnit.PACKAGE
            lc.contains("компл") ->PositionUnit.SET
            lc == "м"||lc == "м." ->PositionUnit.METER
            lc == "пласт." ->PositionUnit.PLATE
            else -> throw Xeption.forEndUser(L10nMessage("неизвестная единица измерения $strValue"))
        }
    }
}