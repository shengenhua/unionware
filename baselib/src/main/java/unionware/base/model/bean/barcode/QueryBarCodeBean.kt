package unionware.base.model.bean.barcode

/**
 * 带有查询项目基础数据 的条码
 */
class QueryBarCodeBean(
    code: String
) : BarCodeBean(code) {

    /**
     * id
     */
    var id: String? = null

    /**
     * 名
     */
    var name: String? = null

    /**
     * 提示显示的名称
     */
    var queryName: String? = null

    /**
     * id
     */
    var showQuery: Boolean = true
}