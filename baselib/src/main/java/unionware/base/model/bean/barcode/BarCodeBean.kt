package unionware.base.model.bean.barcode

import com.google.gson.annotations.SerializedName
import unionware.base.model.bean.CommonListBean
import unionware.base.ext.tryBigDecimalToZeros

/**
 * 通用 的扫描条码实体类
 */
open class BarCodeBean(
    /**
     *  上传的值
     */
    var code: String,
) {
    val value: String?
        get() {
            return materialCode
        }

    /**
     * 数量
     */
    var qty: String? = null
        get() {
            return field?.tryBigDecimalToZeros() ?: field
        }

    /**
     * 开始时间
     */
    var startTime: String? = null

    /**
     * 结束时间
     */
    var endTime: String? = null

    /**
     *  物料Id
     */
    @SerializedName(value = "materialId", alternate = ["MaterialId"])
    var materialId: String? = null

    /**
     * 物料编码
     */
    var materialCode: String? = null

    /**
     * 物料名称
     */
    var materialName: String? = null

    /**
     * 规格型号
     */
    var materialSpec: String? = null

    /**
     * 详情信息
     */
    var infoList: List<unionware.base.model.bean.CommonListBean>? = null
}