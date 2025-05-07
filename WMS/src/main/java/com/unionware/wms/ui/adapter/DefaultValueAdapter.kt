package com.unionware.wms.ui.adapter

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.PropertyBean

class DefaultValueAdapter :
    BaseQuickAdapter<PropertyBean, BaseViewHolder>(R.layout.adapter_default_value) {
    override fun convert(holder: BaseViewHolder, item: PropertyBean) {
        holder.setText(R.id.tv_scan_title, item.name)

        holder.getView<EditText>(R.id.et_scan_input).apply {
            setText(item.value)

            isEnabled = item.isEnable
            inputType = when (item.type) {
                "INTEGER" -> EditorInfo.TYPE_CLASS_NUMBER
                "DECIMAL" -> {
                    EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                }

                else -> EditorInfo.TYPE_CLASS_TEXT
            }
            if (isFocusable) {
                item.value?.length?.let { setSelection(it) }
            }
        }.also {
            it.setOnKeyListener { v, keyCode, event ->
                if (event != null &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN
                ) {
                    focusPos(holder.layoutPosition + 1)
                    return@setOnKeyListener true
                }
                false
            }
        }
    }

    private fun focusPos(position: Int) {
        if (position < data.size) {
            val editText: EditText = getViewByPosition(position, R.id.et_scan_input) as EditText
            editText.requestFocus()
        } else {
            //关闭输入法
        }
    }

    fun getEditValue(key: String): String {
        val editText = getViewByPosition(
            data.withIndex().first { it.value.key == key }.index,
            R.id.et_scan_input
        ) as EditText
        return editText.text.toString()
    }
}