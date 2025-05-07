package com.unionware.virtual.view.adapter.virtual

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterVirtualSpinnerBinding
import unionware.base.databinding.ItemVirSpinnerBinding
import unionware.base.model.bean.PropertyBean
import com.unionware.basicui.view.spinner.SpinnerPop


class VirSpinnerItemListener :
    BaseMultiItemAdapter.OnMultiItem<PropertyBean, VirSpinnerItemListener.SpinnerItemDataBinding>() {
    open class SpinnerItemDataBinding(binding: AdapterVirtualSpinnerBinding) :
        DataBindingHolder<AdapterVirtualSpinnerBinding>(binding) {
        var spinnerPop: SpinnerPop? = null
    }

    /**
     * 选项回车事件的回调接口
     */
    var onSpinnerChangeListener: ((text: String, bean: PropertyBean?, position: Int) -> Unit)? =
        null

    /**
     * 显示窗口 获取焦点
     */
    var onFocusShowListener: (() -> Unit)? = null


    override fun onBind(
        holder: SpinnerItemDataBinding, position: Int, item: PropertyBean?,
    ) {
        holder.binding.apply {
            this.item = item
            ivDelete.apply {
                visibility = if (item?.value == null) View.GONE else View.VISIBLE
                setOnClickListener {
                    etSpinner.text = ""
                    onSpinnerChangeListener?.invoke(
                        "",
                        item?.also { it.value = "" },
                        position
                    )
                }
            }
            ivInfo.also {
                it.setOnClickListener {
                    if (holder.spinnerPop?.isShowing() == true) {
                        holder.spinnerPop?.dismiss()
                    } else {
                        onFocusShowListener?.invoke()
                        holder.spinnerPop?.show()
                    }
                }
            }
            etSpinner.apply {
                holder.spinnerPop = SpinnerPop(context, etSpinner).apply {
                    setBaseAdapter(
                        SpinnerStrAdapter(context, item?.enums ?: mutableListOf())
                    ) { position, t ->
                        t?.get("Value").toString().also {
                            val newItem = item?.clone()
                            newItem?.value = it
                            onSpinnerChangeListener?.invoke(it, newItem, position)
//                                etSpinner.text = t?.get("Name")
                        }
                    }
                }
                setOnClickListener {
                    ivInfo.performClick()
                }
                if (item?.value.isNullOrEmpty()) {
                    text = ""
                } else {
                    item?.enums?.withIndex()?.forEach {
                        if (it.value["Value"] == item.value) {
                            text = it.value["Name"]
                        }
                    }
                }
            }

        }
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int,
    ): SpinnerItemDataBinding {
        return SpinnerItemDataBinding(
            AdapterVirtualSpinnerBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}

class VirSpinnerAdapter(context: Context, val array: List<Map<String, String>>?) :
    ArrayAdapter<String>(
        context,
        android.R.layout.simple_spinner_item,
        array?.map { it["Name"] } ?: listOf()
    ) {
    /*override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflate(LayoutInflater.from(context), parent, false)
        view.tvName.text = array?.get(position)?.get("Name")
        return view.root
    }*/

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = ItemVirSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
        view.tvName.text = array?.get(position)?.get("Name")
        return view.root
    }
}