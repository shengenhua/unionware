package com.unionware.wms.model.resp

import com.google.gson.annotations.SerializedName
import unionware.base.ext.tryBigDecimalToZeros

/**
 * Author: sheng
 * Date:2025/3/27
 */
class BarcodesResp {
    @SerializedName("barCode", alternate = ["BarCode"])
    var barCode: String? = null

    @SerializedName("barCodeId", alternate = ["BarCodeId"])
    var barCodeId: String? = null
        get() {
            return field.tryBigDecimalToZeros()
        }

    @SerializedName("qty", alternate = ["Qty"])
    var qty: String? = null
        get() {
            return field.tryBigDecimalToZeros()
        }

    @SerializedName("seq", alternate = ["Seq"])
    var seq: String? = null
        get() {
            return field.tryBigDecimalToZeros()
        }
}