package unionware.base.model.bean.barcode

import unionware.base.model.bean.CommonListBean
import unionware.base.model.bean.PropertyBean
import unionware.base.ext.tryBigDecimalToZeros


/**
 * 通用 的扫描条码实体类
 */
open class DynamicEntryBean{
    /**
     * 标题
     */
    var tag: String? = null
    var tagValue: String? = null
        get() {
            return field?.tryBigDecimalToZeros() ?: field
        }

    var list: List<unionware.base.model.bean.CommonListBean>? = null
    var viewList: List<PropertyBean>? = null
}