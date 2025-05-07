package unionware.base.model.bean

/**
 * Author: sheng
 * Date:2024/12/2
 */
class EntryAndSubBean {
    var entry: EntryView = EntryView()
    var subEntry: EntryView = EntryView()

    data class EntryView(
        /**
         * 判断是否显示单据体
         */
        var isShow: Boolean = true,
        /**
         * 判断是否显示 扫描框
         */
        var hasBarCode: Boolean = false,
        var hasBarQty: Boolean = false,
    )
}
