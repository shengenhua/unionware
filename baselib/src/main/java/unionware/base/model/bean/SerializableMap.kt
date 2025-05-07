package unionware.base.model.bean

import java.io.Serializable

/**
 * Author: sheng
 * Date:2024/8/19
 */
class SerializableMap : Serializable {
    /**
     * 数据
     */
    var map: Map<String, String>? = null

    /**
     * 回显视图
     */
    var list: List<CommonListBean>? = null
}
