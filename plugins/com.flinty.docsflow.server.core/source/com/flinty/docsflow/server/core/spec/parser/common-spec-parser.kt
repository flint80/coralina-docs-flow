/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.flinty.docsflow.server.core.spec.parser

import com.flinty.docsflow.common.core.model.domain.SpecificationPositionUnit
import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption

class ParsedSpecificationPosition {

    lateinit var article: String

    lateinit var name: String

    lateinit var supplier: String

    lateinit var amount: java.math.BigDecimal

    lateinit var unit: com.flinty.docsflow.common.core.model.domain.SpecificationPositionUnit

    var amountNote: java.math.BigDecimal? = null

    var unitNote: com.flinty.docsflow.common.core.model.domain.SpecificationPositionUnit? = null

    lateinit var storeAmount: java.math.BigDecimal

    lateinit var toBeOrdered: java.math.BigDecimal
}

object SpecParcerUtils{
    fun getUnit(strValue:String):SpecificationPositionUnit{
        val lc = strValue.toLowerCase()
        return when{
            lc.contains("шт") ->SpecificationPositionUnit.PIECE
            lc.contains("уп") ->SpecificationPositionUnit.PACKAGE
            lc.contains("компл") ->SpecificationPositionUnit.SET
            lc == "м"||lc == "м." ->SpecificationPositionUnit.METER
            else -> throw Xeption.forEndUser(L10nMessage("неизвестная единица измерения $strValue"))
        }
    }
}