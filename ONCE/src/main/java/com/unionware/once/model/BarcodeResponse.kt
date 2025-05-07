package com.unionware.once.model

import com.google.gson.annotations.SerializedName
import unionware.base.ext.bigDecimalToZeros


/**
 * Author: sheng
 * Date:2024/12/25
 */
class BarcodeResponse(
    val jarray: List<Jarray> = emptyList(),
) {
    class Jarray {

        @SerializedName("_items")
        val items: MutableList<MutableMap<String, Any>> = mutableListOf()
            get() {
                field.forEach {
                    it.forEach { (key, value) ->
                        it[key] = value.bigDecimalToZeros()
                    }
                }
                return field
            }

        val params: MutableMap<String, Any> = mutableMapOf()
            get() {
                field.forEach { (key, value) ->
                    when (key) {
                        "count" -> {
                            field[key] = value.bigDecimalToZeros().toInt()
                        }

                        else -> {
                            field[key] = value.bigDecimalToZeros()
                        }
                    }
                }
                return field
            }

        val formId: String? = null
        val server: String? = null
    }
}
