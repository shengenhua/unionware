package unionware.base.model

import android.text.InputFilter
import androidx.databinding.BaseObservable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import unionware.base.model.bean.CommonListBean

class ViewDisplay(

    /**
     *  显示的标题 name
     */
    var title: String,
    /**
     * 本地标识
     */
    var tag: String,
    /**
     *  上传的key name
     */
    var key: String? = null,
    /**
     * 基础数据 接口
     */
    var code: String? = null,
    /**
     * 是否能编辑
     */
    var isEdit: Boolean = false,
    /**
     * 是否必填
     */
    var isRequired: Boolean = false,
    /**
     * 是否只能输入 小数 和 整数
     */
    var isNumber: Boolean = false,
) : BaseObservable() {
    /**
     * 对应保存的数据 | 显示的内容
     */
    var value: String? = null

    /**
     * 特殊的 上传数据 如  基础资料 需要上传id
     */
    var id: String? = null

    /**
     * 特殊的 基础数据 查询 标签
     */
    var parentName: String? = "parentId"

    /**
     * 特殊的 基础数据 查询 标签
     */
    var parentId: String? = null

    /**
     * 焦点
     */
    var focusable: Boolean = false

    /**
     * 输入的模式
     */
    var inputType: Int? = null

    /**
     * 详情信息
     */
    var infoList: List<CommonListBean>? = null

    /**
     * 携带显示的适配器
     */
    var carryAdapter: RecyclerView.Adapter<ViewHolder>? = null

    /**
     * 编辑 但是确认后无法编辑
     */
    var isEditVerify: Boolean = false

    /**
     * 编辑 输入校验
     */
    var inputFilters: Array<InputFilter>? = null

    /**
     * 显示类型
     *  0 = 显示
     *  1 = 输入
     *  2 = 选择
     */
    var type: Int = 0
        get() {
            return if (isEdit) {
                1
            } else {
                field
            }
        }
        set(value) {
            if (value == 2) {
                isEdit = false
                isRequired = true
            }
            field = value
        }

}