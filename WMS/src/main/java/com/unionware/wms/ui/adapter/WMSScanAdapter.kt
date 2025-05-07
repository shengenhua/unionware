package com.unionware.wms.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import com.unionware.wms.URLPath
import unionware.base.model.bean.BarcodeBean
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.bean.PropertyBean
import java.math.BigDecimal


open class WMSScanAdapter :
    BaseQuickAdapter<PropertyBean?, WMSScanAdapter.WMSScanViewHolder>(R.layout.item_scan_input_content) {
    var onEditorActionChangeListener: OnEditorActionChangeListener? = null
    val MIN_DELAY_TIME: Int = 200
    var formId: String? = null
    companion object {
        var lastClickTime: Long = 0
    }
    class WMSScanViewHolder(view: View) : BaseViewHolder(view) {
        var textWatcher: TextWatcher? = null
        var runnble: Runnable? = null
    }
    var addShow: MutableList<String> = ArrayList()
    var deadLockList: MutableList<String> = ArrayList()

    var markPosition: Int = 0

    private val garCodeGroup = setOf("FBarCodeId_Proxy", "FBoxCodeId_Proxy", "FNewCodeId")
    private val baseDataGroup = setOf("BASEDATA", "ASSISTANT","COMBOBOX","COMBOX","CHECKBOX","RADIOBOX","ITEMCLASS","DATETIME","FLEXVALUE")

    @SuppressLint("SetTextI18n")
    override fun convert(holder: WMSScanAdapter.WMSScanViewHolder, item: PropertyBean?) {
        val isBaseData = baseDataGroup.contains(item!!.type)
        //日期不需要基础资料 显示
        val isDataTime = "DATETIME" == item.type
        //条码不需要基础资料 显示
        val isBarCode = garCodeGroup.contains(item.key)

        holder
            .setText(R.id.tv_scan_title, item.name)
            .setGone(R.id.iv_base_info_query, !isBaseData || isDataTime || isBarCode)
            .setGone(R.id.tv_scan_default, true)
            .setGone(R.id.iv_add, !addShow.contains(item.key))

        holder.getView<EditText>(R.id.et_scan_input).also {
            it.removeTextChangedListener(holder.textWatcher)
            it.onFocusChangeListener = null
            it.setOnEditorActionListener(null)

            if(isDeadLock(item.key)){
                it.isEnabled = false
            }else {
                it.isEnabled = item.isEnable && !item.isLock && !isDataTime
            }
            //如果是装箱的已装件数，默认锁定，不让放开
            //装箱 的包装容量和条码数量 默认锁定，可以手动开启
            if (formId!=null && formId == URLPath.WMS.MENU_WMS_PACKING_FROM_ID && item.key == "FCount_Proxy"){
                it.isEnabled = false
            }

            holder.setImageResource(
                R.id.tv_scan_lock,
                if (!it.isEnabled) unionware.base.R.drawable.dw_lock else unionware.base.R.drawable.dw_unlock
            )
            it.inputType = when (item.type) {
                "INTEGER" -> EditorInfo.TYPE_CLASS_NUMBER
                "DECIMAL" -> EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                else -> EditorInfo.TYPE_CLASS_TEXT
            }

            if (markPosition == holder.layoutPosition) {
                if (it.isEnabled) {
                    it.requestFocus()
                } else {
                    markPosition++
                }
            }
            if (item.value == null) {
                it.setText("")
            } else {
                if (item.type == "DECIMAL" || item.type == "INTEGER") {
                    if(item.type == "INTEGER"){
                        if(item.value.isEmpty()){
                            item.value = "0"
                        }else {
                            item.value = BigDecimal(item.value).toInt().toString();
                        }
                    }else {
                        if(BigDecimal(item.value).toDouble() == 0.0){
                            item.value = "0"
                        }else {
                            item.value = BigDecimal(item.value).stripTrailingZeros().toPlainString()
                        }
                    }
                }
                item.value?.also { text ->
                    run {
                        it.setText(text)
                        if (it.isFocusable)
                            it.setSelection(text.length)
                    }
                }
            }
            it.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    markPosition = holder.layoutPosition
                }
            }
            it.setOnEditorActionListener { textView, i, keyEvent ->
                getItem(holder.layoutPosition)!!.value = textView.text.toString()
                if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (keyEvent.action == KeyEvent.ACTION_DOWN && !isFastClick()) {
                        onEditorActionChangeListener?.onEditorActionListener(
                            it, item, holder.layoutPosition
                        )
                    }
                } else if (!isFastClick()) {
                    onEditorActionChangeListener?.onEditorActionListener(
                        it, item, holder.layoutPosition
                    )
                }
                true
            }
            it.tag = holder.layoutPosition
            holder.textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // 文本改变之前的处理逻辑
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    // 文本正在改变的处理逻辑
                }

                override fun afterTextChanged(s: Editable) {
                    //text 更新值,为了处理不回车，更新视图值
                    Log.e("文本改变", "s=" + s.toString())
                    val isBaseData = baseDataGroup.contains(item!!.type)
                    //日期不需要基础资料 显示
                    val isDataTime = "DATETIME" == item.type
                    //条码不需要基础资料 显示
                    val isBarCode = garCodeGroup.contains(item.key)
                    if (!isBaseData && !isDataTime && !isBarCode && holder.layoutPosition == holder.getView<EditText>(R.id.et_scan_input).tag) {
                        setValueOnly(item.key, s.toString())
                    }

                }
            }
            if (!isBaseData && !isDataTime && !isBarCode) {
                it.addTextChangedListener(holder.textWatcher)
            }
        }
    }


    fun getTextByKey(key: String): String {
        val position = data.withIndex().firstOrNull { key == (it.value?.key) }?.index
        if (position == null) {
            return ""
        }
        val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
        return editText.text?.toString() ?: ""
    }

    fun setValueOnly(tag: String, content: String) {
        val list = data.filter { it?.key.equals(tag, true) }
        if (list.isEmpty()) return
        list[0]!!.value = content
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


    fun setValue(tag: String, bean: BarcodeBean) {
        data.withIndex().firstOrNull {
            it.value?.key.equals(tag)
        }?.also {
            it.value?.isEnable = bean.isEnabled
            it.value?.value =
                if (it.value?.type == "ASSISTANT" || it.value?.type == "BASEDATA")
                    bean.number
                else
                    bean.value

            notifyItemChanged(it.index)
        }
    }
    open fun setEnabledLock(tag:String){
        deadLockList.add(tag)
    }
    open fun removeEnabledLock(tag:String){
        deadLockList.remove(tag);
    }
    fun isDeadLock(tag:String):Boolean{
        return deadLockList.contains(tag)
    }
    fun setDataInfo(position: Int, infoBean: BaseInfoBean) {
        data[position]?.value = if (null == infoBean.name) infoBean.code else infoBean.name
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
            .firstOrNull { "combox" != it.value!!.type && (null == it.value?.value || "" == it.value!!.value) && !it.value!!.isLock }?.index
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


    //检查必填项，如果有必填项为空的，返回pos 请求焦点，通过返回-1
    fun checkRequired(): Int {
        return data.indexOfFirst {
            "1".equals(it!!.isEnable) && (null == it?.value || "".equals(
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
     * 光标下移
     */
    fun focusMoveDown(position: Int) {
        markPosition = position + 1
        if (markPosition >= data.size) {
            markPosition = position
            getViewByPosition(markPosition, R.id.et_scan_input).also {
                if (it == null) {
                    return
                }
                val inputManger =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManger.hideSoftInputFromWindow(it.windowToken, 0)
                it.clearFocus()
            }
        } else {
            notifyItemChanged(markPosition)
        }
    }

    fun setEditTextValue(position: Int, content: String = "") {
        if (position < data.size) {
            val editText: EditText? = getViewByPosition(position, R.id.et_scan_input) as? EditText
            editText?.setText(content);
            editText?.setSelection(content.length)
            editText?.requestFocus()

            if (content.isNotEmpty()) {
                onEditorActionChangeListener?.onEditorActionListener(
                    editText, data[position]!!, position
                )
            }
        }
    }

    fun getPosByKey(key: String): Int {
        return data.indexOfFirst {
            it?.key == key
        }
    }

    /**
     * 定义 RecyclerView 选项单击事件的回调接口
     */
    interface OnEditorActionChangeListener {
        fun onEditorActionListener(view: EditText?, bean: PropertyBean, position: Int)
    }
}