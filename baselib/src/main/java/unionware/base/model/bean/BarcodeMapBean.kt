package unionware.base.model.bean

import unionware.base.model.bean.CommonListBean


/**
 * Author: sheng
 * Date:2024/9/23
 */
class BarcodeMapBean(
    /**
     * 标识
     */
    var tag: String,
) {
    var tagName: String? = null

    /**
     * 是否选中
     */
    var isSelect: Boolean = false
    var list: List<unionware.base.model.bean.CommonListBean>? = null
    var value: Map<String, Any>? = null
}