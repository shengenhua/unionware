package unionware.base.model.bean

import unionware.base.ext.tryBigDecimalToZeros


/**
 * Author: sheng
 * Date:2024/12/2
 */
class DynamicConfigBean {
    var code: String? = null
    var id: String? = null
        get() = field.tryBigDecimalToZeros() ?: field
    var name: String? = null

    var params: Map<String, Any>? = null
    var items: List<JobItem>? = null

    class JobItem {
        var code: String? = null
        var id: String? = null
            get() = field.tryBigDecimalToZeros() ?: field
        var name: String? = null
        var typeId: String? = null

        var itemColor = 0xFF1E89EF.toInt()
    }
}