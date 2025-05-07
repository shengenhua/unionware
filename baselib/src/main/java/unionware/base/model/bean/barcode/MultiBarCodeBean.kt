package unionware.base.model.bean.barcode

import unionware.base.model.bean.CollectMultiItem

/**
 * 带有采集项目 和 气体 的条码
 */
open class MultiBarCodeBean(
    code: String
) : BarCodeBean(code) {
    /**
     * 气体id
     */
    var gasId: String? = null
    /**
     * 气体
     */
    var gasName: String? = null

    /**
     * 采集项目
     */
    var collects: List<CollectMultiItem>? = null
}