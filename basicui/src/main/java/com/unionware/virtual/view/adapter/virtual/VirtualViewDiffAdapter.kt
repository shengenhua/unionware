package com.unionware.virtual.view.adapter.virtual

import androidx.recyclerview.widget.DiffUtil
import com.unionware.basicui.R
import unionware.base.model.bean.PropertyBean
import unionware.base.ext.tryBigDecimalToZeros

class VirtualViewDiffAdapter :
    VirMultiItemAdapter<PropertyBean>(object : DiffUtil.ItemCallback<PropertyBean>() {
        override fun areItemsTheSame(oldItem: PropertyBean, newItem: PropertyBean): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: PropertyBean, newItem: PropertyBean): Boolean {
            return oldItem.isEnable == newItem.isEnable && oldItem.isVisible == newItem.isVisible &&
                    oldItem.value.tryBigDecimalToZeros() == newItem.value.tryBigDecimalToZeros() &&
                    oldItem.valueName.tryBigDecimalToZeros() == newItem.valueName.tryBigDecimalToZeros() &&
                    oldItem.valueNumber.tryBigDecimalToZeros() == newItem.valueNumber.tryBigDecimalToZeros()
        }
    }) {

    var itemUpdateValue: ItemUpdateValue? = null
    var queryItemListener: ((PropertyBean, position: Int) -> Unit)? = null
    var itemFocus: ((Boolean) -> Unit)? = null

    fun setItemUpdateValue(itemUpdateValue: (item: PropertyBean?) -> Unit) {
        this.itemUpdateValue = object : ItemUpdateValue {
            override fun updateValue(item: PropertyBean?) {
                itemUpdateValue.invoke(item)
            }
        }
    }

    private var editItemListener = VirEditItemListener()

    init {
        addItemType(0, editItemListener.apply {
            onEditorActionChangeListener = { editText, propertyBean, postion ->
                itemFocus?.invoke(true)
                items[postion].value = propertyBean?.value.toString()
                itemUpdateValue?.updateValue(propertyBean)
            }
        })
        addItemType(1, VirTimeItemListener().apply {
            onTimeActionChangeListener = { time, propertyBean, position ->
                items[position].value = propertyBean?.value.toString()
                itemUpdateValue?.updateValue(propertyBean)
            }
            onFocusShowListener = {
                itemFocus?.invoke(true)
            }
        })
        addItemType(2, VirCheckItemListener().apply {
            onCheckedChangeListener = { check, propertyBean, postion ->
                propertyBean?.value = check.toString()
                items[postion].value = propertyBean?.value.toString()
                itemUpdateValue?.updateValue(propertyBean)
            }
        })

        addItemType(3, VirSpinnerItemListener().apply {
            onSpinnerChangeListener = { value, propertyBean, postion ->
//                items[postion].value = propertyBean?.value.toString()
                itemUpdateValue?.updateValue(propertyBean)
            }
            onFocusShowListener = {
                itemFocus?.invoke(true)
            }
        })
        /*addItemType(4, VirEditItemListener().apply {
            onEditorActionChangeListener = { editText, propertyBean, postion ->
                itemUpdateValue?.updateValue(propertyBean)
            }
        })
        addItemType(5, VirEditItemListener().apply {
            onEditorActionChangeListener = { editText, propertyBean, postion ->
                itemUpdateValue?.updateValue(propertyBean)
            }
        })*/

        addOnItemChildClickListener(R.id.ivScanLock) { adapter, view, position ->
            adapter.getItem(position)?.apply {
                isLock = !isLock
                notifyItemChanged(position)
            }
        }
        addOnItemChildClickListener(R.id.ivQuery) { adapter, _, position ->
            adapter.getItem(position)?.apply {
                itemFocus?.invoke(true)
                queryItemListener?.invoke(this, position)
            }
        }
    }

    fun submitListNotFocus(list: List<PropertyBean>?) {
        val newList = list?.map { it.clone() }
        super.submitList(newList)
    }

    fun submitList(list: List<PropertyBean>?, focus: Boolean) {
        val newList = list?.map { it.clone() }
        super.submitList(newList) {
            if (focus) {
                editItemListener.focusPosition(cursorPosition(newList))
            }
        }
    }

    override fun submitList(list: List<PropertyBean>?) {
        val newList = list?.map { it.clone() }
        super.submitList(newList) {
            editItemListener.focusPosition(cursorPosition(newList))
        }
    }

    private fun setCursorPosition(position: Int) {
        editItemListener.focusPosition(position)
    }

    fun setCursorPosition() {
        editItemListener.focusPosition(cursorPosition(items))
    }

    private fun cursorPosition(list: List<PropertyBean>?): Int {
        list?.withIndex()?.forEach {
            if ((it.value.value.isNullOrEmpty() || it.value.value?.trim()
                    .isNullOrEmpty() || it.value.value.trim().tryBigDecimalToZeros() == "0") // 未填写
                && it.value.isEnable && !it.value.isLock // 可编辑
                && editType.contains(it.value.type) // 是输入的item  可以获取光标的view
            ) {
                return it.index
            }
        }
        return -1
    }

    interface ItemUpdateValue {
        /**
         * 修改了内容需要 更新
         */
        fun updateValue(item: PropertyBean?)
    }

    private val editType = setOf(/*多类别资料*/"ITEMCLASS",
        /*多维度字段：辅助属性、仓位*/"RELATEDFLEX",
        /*基础资料*/"BASEDATA",
        /*辅助资料*/"ASSISTANT",
        /*基础资料类型*/"FLEXVALUE",
        /*文本*/"TEXT",
        /*整数*/"INTEGER",
        /*小数*/"DECIMAL"
    )

    override fun getItemViewType(position: Int, list: List<PropertyBean>): Int {
        return when (list[position].type) {

            //  /*多类别资料*/"ITEMCLASS",//-> 4

            //  /*多维度字段：辅助属性、仓位*/"RELATEDFLEX",// -> 5

            /**/"FLEXVALUE",/*基础资料*/"BASEDATA",/*辅助资料*/"ASSISTANT",/*文本*/"TEXT",/*整数*/"INTEGER",/*小数*/"DECIMAL",
                -> 0

            /*日期时间*/"DATETIME", "DATE", "TIME" -> 1

            /*单选框*/"RADIOBOX",/*复选框*/"CHECKBOX" -> 2

            /*下拉列表*/"COMBOX", "COMBOBOX" -> 3

            else -> 0
        }
    }
}