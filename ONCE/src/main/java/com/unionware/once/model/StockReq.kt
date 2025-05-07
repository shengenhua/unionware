package com.unionware.once.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: sheng
 * Date:2025/2/26
 */
class StockReq : Serializable {
    @SerializedName("Stock")
    var stock: StockBean? = null

    @SerializedName("StockLoc")
    var stockLoc: MutableList<MutableMap<String, StockLocBean>>? = null

    class StockBean : Serializable {
        var code: String? = null
        var name: String? = null
        var id: Int? = null

        @SerializedName("FStockFlexItem")
        var fStockFlexItem: MutableList<MutableMap<String, String>>? = null
    }

    class StockLocBean : Serializable {
        var code: String? = null
        var name: String? = null
        var id: Int? = null
    }

}