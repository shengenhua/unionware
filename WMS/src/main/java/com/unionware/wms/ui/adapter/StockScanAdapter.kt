package com.unionware.wms.ui.adapter

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.EntityBean
import unionware.base.model.req.ItemBean

class StockScanAdapter : BaseQuickAdapter<EntityBean, BaseViewHolder>(R.layout.item_scan_input_content) {
    private var onEditorActionChangeListener: OnEditorActionChangeListener? = null
    override fun convert(holder: BaseViewHolder, bean: EntityBean) {
        holder.setText(R.id.tv_scan_title, bean.property!!.name)
        val isVisable =
            "BASEDATA" == bean.property!!.type || "DATETIME" == bean.property.type || "ASSISTANT" == bean.property!!.type
        holder.setVisible(R.id.iv_base_info_query, isVisable)
        val tv_lock = holder.getView<ImageView>(R.id.tv_scan_lock)
        val et_input: EditText = holder.getView(R.id.et_scan_input)
        val tv_default = holder.getView<ImageView>(R.id.tv_scan_default)
        val iv_query = holder.getView<ImageView>(R.id.iv_base_info_query)
        val isBarcode = "FContainer" == bean.property.key || "FLPN" == bean.property.key
        holder.setVisible(R.id.tv_scan_lock, !isBarcode)
        holder.setVisible(R.id.tv_scan_default, !isBarcode)
        tv_lock.setImageResource(if (bean.isLock) unionware.base.R.drawable.dw_lock else unionware.base.R.drawable.dw_unlock)
        tv_default.setImageResource(if (bean.isDefalut) R.drawable.ic_defalut else R.drawable.ic_undefalut) //        tv_lock.set = if (bean.isLock) "已锁定" else "锁定" //        val enable = !bean.isLock && !bean.isDefalut && bean.isEdit // 判断是否可编辑（没有锁定 没有默认 没有可编辑）
        //        tv_default.text = if (bean.isDefalut) "已默认" else "默认"
        val enable = bean.isEdit && !bean.isLock && !bean.isDefalut
        et_input.isEnabled = enable
        iv_query.isEnabled = enable
        et_input.setOnEditorActionListener { textView: TextView?, actionId: Int, keyEvent: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (keyEvent!!.action == KeyEvent.ACTION_UP && !isFastClick()) {
                    onEditorActionChangeListener!!.onEditorActionListener(et_input,
                        bean,
                        holder.layoutPosition)
                }
            } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (!isFastClick()) {
                    onEditorActionChangeListener!!.onEditorActionListener(et_input,
                        bean,
                        holder.layoutPosition)
                }
            }

            true
        }

        //        if ("DECIMAL" == bean.property.type) { // 如果是数字，就变成数字键盘
        //            et_input.inputType = InputType.TYPE_CLASS_NUMBER
        //            if (!bean.value.isNullOrEmpty()) {
        //                bean.value = BigDecimal(bean.value).stripTrailingZeros().toPlainString()
        //            }
        //        }
        et_input.setText(bean.value)
    }

    //设置可编辑性
    fun setEditable(type: String, enable: Boolean) {
        data.filter { (it.property.source == type) }.forEach { it.isEdit = enable }
    }

    fun setEntityEnable(enable: Boolean) {
        data.filter { (it.property.entity == "FEntity") }.forEach { it.isEdit = enable }
    }

    fun resetData() {
        data.filter { (!it.isDefalut) }.forEach {
            it.value = ""
            it.isEdit = true
        }
        notifyDataSetChanged()
    }

    fun clearData() {
        data.filter { (!it.isDefalut && it.property.key != "FContainer") }.forEach {
            val enable = it.property.entity != "FEntity"
            it.value = ""
            it.isEdit = enable
        }
        notifyDataSetChanged()
    }

    fun getDefaultValueList(): MutableList<ItemBean> {
        val list: MutableList<ItemBean> = ArrayList()
        data.filter { (it.isDefalut && !it.isLock && it.isEdit) }.forEach {
            if (null != it.value && it.value.isNotEmpty()) {
                list.add(ItemBean(it.property.key, it.value))
            }

        }
        return list
    }


    fun findCurrentFocusable(pos: Int) {
        var editText: EditText? = null
        val position = data.withIndex()
            .firstOrNull { (null == it.value?.value || "" == it.value!!.value.trim()) && !it.value!!.isLock && !it.value!!.isDefalut && it.value!!.isEdit }?.index
        editText = if (position == null) {
            var index = data.slice(pos + 1 until data.size).withIndex()
                .firstOrNull { !it.value.isDefalut && !it.value.isLock && it.value.isEdit }?.index
                ?: 0

            index = if (index + pos + 1 <data.size) index + pos + 1 else data.size - 1
            getViewByPosition(index, R.id.et_scan_input) as EditText
        } else {
            getViewByPosition(position, R.id.et_scan_input) as EditText
        }
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.postDelayed(Runnable { editText.requestFocus() }, 50)
    }


    fun findCurrentFocusable() {
        var editText: EditText? = null
        val position = data.withIndex()
            .firstOrNull { (null == it.value!!.value || "" == it.value!!.value.trim()) && !it.value!!.isLock && !it.value!!.isDefalut && it.value!!.isEdit }?.index
        editText = if (position == null) {
            getViewByPosition(0, R.id.et_scan_input) as EditText
        } else {
            getViewByPosition(position, R.id.et_scan_input) as EditText
        }
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.postDelayed(Runnable { editText.requestFocus() }, 50)
    }


    fun setFocusable(position: Int) {
        if (position < data.size) {
            val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.postDelayed(Runnable { editText.requestFocus() }, 50)
        }
    }

    //检查必填项，如果有必填项为空的，返回pos 请求焦点，通过返回-1
    fun checkRequired(): Int {
        return data.indexOfFirst {
            it.isRequired && (null == it?.value || "".equals(it.value))
        }
    }

    //检查是满足录入条件 通过返回-1
    fun isFinish(): Int {
        val defaultNum =
            data.count() { ("FContainer" != it.property.key || "FLPN" != it.property.key) && it.isDefalut } // 默认数
        val lockNum =
            data.count() { ("FContainer" != it.property.key || "FLPN" != it.property.key) && it.isLock } // 锁定数
        val nonEditableNum =
            data.count() { ("FContainer" != it.property.key || "FLPN" != it.property.key) && !it.isEdit } // 不可编辑数量

        return defaultNum + lockNum + nonEditableNum
    }


    fun isCanSubmit(pos: Int): Boolean {
        val autoSubmit = data.withIndex().firstOrNull {null !=it.value.value && it.value.isEdit && !it.value.isLock && !it.value.isDefalut }?.index
        return pos == autoSubmit
    }

    /**
     * 定义 RecyclerView 选项单击事件的回调接口
     */
    interface OnEditorActionChangeListener {
        fun onEditorActionListener(view: EditText?, bean: EntityBean?, pos: Int)
    }

    fun setOnEditorActionChangeListener(onEditorActionChangeListener: OnEditorActionChangeListener?) {
        this.onEditorActionChangeListener = onEditorActionChangeListener
    }

    fun isFastClick(): Boolean {
        var flag: Boolean = true
        val currentClickTime: Long = System.currentTimeMillis()
        if ((currentClickTime - ScanAdapter.lastClickTime) >= 200) flag = false
        ScanAdapter.lastClickTime = currentClickTime
        return flag
    }

}