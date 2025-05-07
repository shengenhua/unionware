package com.unionware.wms.ui.adapter

import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.EntityBean

open class ScanAdapter : BaseQuickAdapter<EntityBean?, BaseViewHolder>(R.layout.item_scan_input_content) {
    var onEditorActionChangeListener: OnEditorActionChangeListener? = null
    val MIN_DELAY_TIME: Int = 200

    companion object {
        var lastClickTime: Long = 0
    }

    var markPosition: Int = 0;
    override fun convert(holder: BaseViewHolder, bean: EntityBean?) {
        holder.setText(R.id.tv_scan_title, bean!!.name)
        val editText = holder.getView<EditText>(R.id.et_scan_input)

        var isVisable = bean.properties != null && "BASEDATA" == bean!!.properties!!.type
        holder.setVisible(R.id.iv_base_info_query, isVisable)
        val tv_lock = holder.getView<TextView>(R.id.tv_scan_lock)
        tv_lock.text = if (bean.isLock) "已锁定" else "锁定"
        if (bean.isLock) {
            editText.isEnabled = !bean.isLock
        }

        val tv_default = holder.getView<TextView>(R.id.tv_scan_default)
        tv_default.text = if (bean.isDefalut) "已默认" else "默认"

        if ("combox" == bean.type || "onlyread" == bean.type) {
            val tv_combox = holder.getView<TextView>(R.id.tv_scan_combox)
            tv_combox.text = bean.value
            tv_combox.visibility = View.VISIBLE
            tv_combox.hint = if ("onlyread" == bean.type) "0" else "请选择打印模板"
            editText.visibility = View.INVISIBLE
            tv_lock.visibility = View.INVISIBLE
            tv_default.visibility = View.INVISIBLE
        }

        editText.isEnabled = !bean.isDefalut
        if ("num" == bean.type) {
            editText.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
        }
        editText.setText(bean.value)
        editText.setOnEditorActionListener { textView, i, keyEvent ->
            getItem(holder.layoutPosition)!!.value = textView.text.toString()
            if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (keyEvent.action == KeyEvent.ACTION_DOWN && !isFastClick()) {
                    onEditorActionChangeListener!!.onEditorActionListener(
                        editText, bean, holder.layoutPosition
                    )
                }
            } else if (!isFastClick()) {
                onEditorActionChangeListener!!.onEditorActionListener(
                    editText, bean, holder.layoutPosition
                )
            }
            true
        }


    }

    fun isFastClick(): Boolean {
        var flag: Boolean = true
        val currentClickTime: Long = System.currentTimeMillis()
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) flag = false
        lastClickTime = currentClickTime
        return flag
    }

    fun setValue(tag: String, content: String) {
        val list = data.filter { it?.key.equals(tag, true) }
        if (list.isEmpty()) return
        list[0]!!.value = content
        notifyItemChanged(getItemPosition(list[0]))
    }


    fun resetData() {
        for (bean in data) {
            bean!!.value = ""
        }
        notifyDataSetChanged()
    }

    fun findCurrentFocusable() {
        var editText: EditText? = null
        val position = data.withIndex()
            .firstOrNull {
                "combox" != it.value!!.type
                        && (null == it.value?.value || "" == it.value!!.value)
                        && !it.value!!.isLock && !it.value!!.isDefalut }?.index
        editText = if (position == null) {
            getViewByPosition(0, R.id.et_scan_input) as EditText
        } else {
            getViewByPosition(position, R.id.et_scan_input) as EditText
        }
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
    }


    //根据位置，获取输入框的值
    fun getEditTextString(position: Int): String {
        val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
        return editText.text.toString()
    }

    //根据key,设置相应的值
    fun getEditTextSetValue(key: String, value: String) {
        val position = data.withIndex().first { key == (it.value?.key) }.index
        val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
        val bean: EntityBean? = getItem(position)
        //设置了默认不能赋值
        if (bean!!.isDefalut) return
        editText.setText(value)
        getItem(position)!!.value = value
    }

    //检查必填项，如果有必填项为空的，返回pos 请求焦点，通过返回-1
    fun checkRequired(): Int {
        return data.indexOfFirst {
            "1".equals(it!!.required) && (null == it?.value || "".equals(
                it.value
            ))
        }
    }

    //获取明细条码位置
    fun getDetealisPos(): Int {
        return data.indexOfFirst { "details".equals(it!!.key) }
    }



    //根据key,获取相应的值
    fun getEditTextGetValue(key: String): String {
        val position = data.withIndex().first { key == (it.value?.key) }.index
        val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
        return editText.text.toString()
    }

    //根据position,获取相应的key值
    fun getKey(position: Int): String {
        return data[position]!!.key
    }

    //校验当前界面是否都有值，无值，返回当前第一个输入的name
    fun isEmpty(): String {
        for (i in 0 until data.size) {
            val editText: EditText = getViewByPosition(i, R.id.et_scan_input) as EditText
            if (TextUtils.isEmpty(editText.text)) return data[i]!!.name
        }
        return ""
    }

    fun setFocusable(position: Int) {
        if (position < data.size) {
            val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.setSelection(editText.text.length)
            editText.requestFocus()
        }
    }

    fun setFocusable(key: String) {
        val position = data.withIndex().first { key == (it.value?.key) }.index
        val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.setSelection(editText.text.length)
        editText.requestFocus()
    }

    fun getEditTextByKey(key: String): EditText {
        val position = data.withIndex().first { key == (it.value?.key) }.index
        val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
        return editText
    }

    //获取焦点，全选字符
    fun setSelection(position: Int) {
        if (position < data.size) {
            val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.setSelection(0, editText.text.length)
            editText.requestFocus()
        }
    }

    //标记焦点位置
    fun markFocus() {
        for (i in 0 until data.size) {
            val editText: EditText = getViewByPosition(i, R.id.et_scan_input) as EditText
            if (editText.isFocused) {
                markPosition = i;
                return
            }
        }

    }

    /**
     * 定义 RecyclerView 选项单击事件的回调接口
     */
    interface OnEditorActionChangeListener {
        fun onEditorActionListener(view: EditText?, bean: EntityBean, position: Int)
    }
}