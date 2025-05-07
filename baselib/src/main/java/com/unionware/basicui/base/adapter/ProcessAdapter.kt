package com.unionware.basicui.base.adapter

import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.LayoutRes
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.lxj.xpopup.XPopup
import unionware.base.databinding.AdapterProcessBinding
import com.unionware.virtual.view.adapter.CommonAdapter
import unionware.base.model.bean.CollectSelectBean
import unionware.base.model.ViewDisplay
import java.util.stream.Collectors


class ProcessAdapter : BaseQuickAdapter<ViewDisplay, ProcessAdapter.ProcessDataBinding>() {

    open class ProcessDataBinding : DataBindingHolder<AdapterProcessBinding> {
        constructor(itemView: View) : super(itemView) {
        }

        constructor(binding: AdapterProcessBinding) : super(binding) {
        }

        constructor(@LayoutRes resId: Int, parent: ViewGroup) : super(resId, parent) {
        }

        var textWatcher: TextWatcher? = null
    }

    private var mOnEditorActionArray: ArrayMap<String, OnItemEditorActionListener>? = null

    fun addOnEditorActionArray(vararg tag: String, listener: OnItemEditorActionListener) = apply {
        mOnEditorActionArray =
            (mOnEditorActionArray ?: ArrayMap<String, OnItemEditorActionListener>()).apply {
                tag.forEach {
                    put(it, listener)
                }
            }
    }

    private var mOnSelectArray: ArrayMap<String, OnItemSelectListener>? = null
    fun addOnSelectArray(vararg tag: String, listener: OnItemSelectListener) = apply {
        mOnSelectArray =
            (mOnSelectArray ?: ArrayMap<String, OnItemSelectListener>()).apply {
                tag.forEach {
                    put(it, listener)
                }
            }
    }

    override fun onBindViewHolder(
        holder: ProcessDataBinding, position: Int, item: ViewDisplay?,
    ) {
        holder.binding.also { bind ->
            item?.also {
                bind.item = it
                bind.ivQuery.visibility =
                    if (it.code?.isNotEmpty() == true) View.VISIBLE else View.GONE

                bind.ivCheckDelete.visibility = if (it.isEdit) {
                    View.VISIBLE
                } else {
                    if (it.isEditVerify) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }

                bind.ivArrowDown.visibility = if (it.type == 2) View.VISIBLE else View.GONE
            }


            bind.ivCheckDelete.setOnClickListener {
                mOnEditorActionArray?.get(item?.tag)
                    ?.onItemEditor(this@ProcessAdapter, position, "")
                bind.etProcessInput.setText("")
                item?.infoList = mutableListOf()
                if (item?.carryAdapter == null) {
                    holder.binding.rvInfo.adapter = CommonAdapter(items = mutableListOf())
                }
                item?.id = null
            }
            if (item?.type == 2) {
                bind.ivArrowDown.setOnClickListener {
                    showSelectDialog(item, position)
                }
                bind.etProcessInput.setOnClickListener {
                    showSelectDialog(item, position)
                }
                bind.etProcessInput.isEnabled = true
            } else {
                bind.etProcessInput.isEnabled = item?.isEdit ?: false
                bind.ivArrowDown.setOnClickListener {
                }
                bind.etProcessInput.setOnClickListener {

                }
            }

            bind.etProcessInput.apply {
                inputType = if (item?.isNumber == true) {
                    EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL //or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                } else InputType.TYPE_CLASS_TEXT //or InputType.TYPE_TEXT_FLAG_MULTI_LINE

                item?.inputType?.apply {
                    inputType = this
                }
                item?.inputFilters?.apply {
                    setFilters(this)
                }
                setOnEditorActionListener { v, actionId, event ->
                    mOnEditorActionArray?.get(item?.tag)
                        ?.onItemEditor(this@ProcessAdapter, position, v.text.toString())
                    return@setOnEditorActionListener false
                }
                holder.textWatcher?.also {
                    removeTextChangedListener(it)
                }
                holder.textWatcher = addTextChangedListener {
                    item?.value = it.toString()
                }

                isFocusable = item?.isEdit ?: false
                isFocusableInTouchMode = item?.isEdit ?: false
                if (item?.focusable == true) {
                    item.focusable = false
                    requestFocus()
                }
            }

            if (item?.carryAdapter != null) {
                holder.binding.rvInfo.layoutManager = LinearLayoutManager(context)
                holder.binding.rvInfo.adapter = item.carryAdapter
                holder.binding.rvInfo.visibility = View.VISIBLE
            } else if (item?.infoList.isNullOrEmpty()) {
                holder.binding.rvInfo.adapter = CommonAdapter(mutableListOf())
                holder.binding.rvInfo.visibility = View.GONE
            } else {
                holder.binding.rvInfo.visibility = View.VISIBLE
                item?.infoList?.also {
                    holder.binding.rvInfo.layoutManager = GridLayoutManager(context, 2).apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if (it.size % 2 == 1 && position == it.size - 1) {
                                    2
                                } else {
                                    1
                                }
                            }
                        }
                    }
                    holder.binding.rvInfo.adapter = CommonAdapter(items = it)
                }
            }
        }
    }


    private fun showSelectDialog(item: ViewDisplay?, position: Int) {
        if (mOnSelectArray?.containsKey(item?.tag) == true) {
            mOnSelectArray?.get(item?.tag)?.onItemSelect(this, position)
            return
        }
        //选择弹窗
        val select = mutableListOf(CollectSelectBean.Y, CollectSelectBean.N)
        XPopup.Builder(context).asCenterList(
            item?.title ?: "请选择一项",
            select.stream().map { it.str }.collect(Collectors.toList()).toTypedArray()
        ) { pos: Int, text: String? ->
            item?.value = text
            item?.id = select[pos].toString()
            notifyItemChanged(position)
        }.show()
    }

    fun clearData() {
        items.withIndex().forEach {
            it.value.value = ""
            it.value.id = ""
            if (!it.value.infoList.isNullOrEmpty()) {
                it.value.infoList = null
            }
            notifyItemChanged(it.index)
        }
    }

    fun clearData(tag: String) {
        items.withIndex().firstOrNull { it.value.tag == tag }?.also {
            it.value.value = ""
            it.value.id = ""
            if (!it.value.infoList.isNullOrEmpty()) {
                it.value.infoList = null
            }
            notifyItemChanged(it.index)
        }
    }

    /**
     * 获取 item 更具上传的 key
     */
    fun setFocusable(key: String? = null, tag: String? = null, index: Int? = 0) {
        items.withIndex().firstOrNull {
            key?.apply {
                return@firstOrNull it.value.key == this
            }
            tag?.apply {
                return@firstOrNull it.value.tag == this
            }
            return@firstOrNull it.index == index
        }?.also {
            it.value.focusable = true
            notifyItemChanged(it.index)
        }
    }


    /**
     * 获取 item 更具上传的 key
     */
    fun getItem(key: String? = null, tag: String? = null): ViewDisplay? {
        return items.firstOrNull {
            if (!key.isNullOrEmpty()) {
                it.key == key
            } else if (!tag.isNullOrEmpty()) {
                it.tag == tag
            } else {
                false
            }
        }
    }

    /**
     * 获取 value 更具上传的 key
     */
    fun getItemValue(key: String): String {
        return items.firstOrNull { it.key == key }?.value ?: ""
    }

    /**
     * 获取 value 更具上传的 key
     */
    fun getItemValueByTag(tag: String): String {
        return items.firstOrNull { it.tag == tag }?.value ?: ""
    }


    /**
     * 更新里面的文本
     */
    fun changedItemValue(tag: String, value: String = "") {
        items.withIndex().firstOrNull {
            it.value.tag == tag
        }?.also {
            it.value.value = value
            notifyItemChanged(it.index)
        }
    }


    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): ProcessDataBinding {
        return ProcessDataBinding(
            AdapterProcessBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }


    fun interface OnItemEditorActionListener {
        fun onItemEditor(adapter: BaseQuickAdapter<ViewDisplay, *>, position: Int, text: String)
    }

    fun interface OnItemSelectListener {
        fun onItemSelect(adapter: BaseQuickAdapter<ViewDisplay, *>, position: Int)
    }
}