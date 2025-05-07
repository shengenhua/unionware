package unionware.base.model.bean

import com.google.gson.annotations.SerializedName
import unionware.base.ext.bigDecimalToZeros

/**
 * 采集 + 检验 方案 数据格式
 */
open class CollectMultiItem {
    @SerializedName(value = "colSeq", alternate = ["ckiSeq"])
    var colSeq: Int? = 0

    @SerializedName(value = "code")
    var code: String? = null

    @SerializedName(value = "colId", alternate = ["ckiId"])
    var colId: String? = null
        get() {
            return field?.bigDecimalToZeros() ?: field
        }

    /**
     * MultiItemType
     */
    @SerializedName(value = "colMethod", alternate = ["ckiMethod"])
    var colMethod: Int? = null

    @SerializedName(value = "colName", alternate = ["ckiName"])
    var colName: String? = null

    @SerializedName(value = "colNumber", alternate = ["ckiNumber"])
    var colNumber: String? = null
        get() {
            try {
                return field?.bigDecimalToZeros() ?: field
            } catch (_: Exception) {
            }
            return field
        }

    @SerializedName(value = "id", alternate = ["checkItemId"])
    var id: String? = null
        get() {
            return field?.bigDecimalToZeros() ?: field
        }
    var name: String? = null
    var tag: String? = null

    var valueText: String? = null

    @SerializedName(value = "value", alternate = ["checkValue"])
    var value: String? = null

    /**
     * 标准值 默认值
     */
    @SerializedName(value = "stdValue", alternate = ["standardValue"])
    var stdValue: String? = null
        get() {
            if (colMethod == 1 || colMethod == 4) try {
                return field?.bigDecimalToZeros() ?: field
            } catch (_: Exception) {
            }
            return field
        }

    /**
     * 值=0 为否，值=1 为 是
     */
    @SerializedName(value = "isWeight", alternate = ["isweight"])
    private val isWeight: String? = null
}

enum class MultiItemType(val type: Int) {
    EDIT(1), SELECT(2), FILE(3), EDITTEXT(4), QUERY(99)
}

