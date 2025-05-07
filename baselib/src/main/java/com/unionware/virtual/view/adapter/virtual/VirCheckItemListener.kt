package com.unionware.virtual.view.adapter.virtual

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterVirtualCheckBinding
import unionware.base.model.bean.PropertyBean

class VirCheckItemListener :
    BaseMultiItemAdapter.OnMultiItem<PropertyBean, VirCheckItemListener.CheckItemDataBinding>() {
    open class CheckItemDataBinding(binding: AdapterVirtualCheckBinding) :
        DataBindingHolder<AdapterVirtualCheckBinding>(binding) {
    }

    /**
     * 选项回车事件的回调接口
     */
    var onCheckedChangeListener: ((isChecked: Boolean, bean: PropertyBean?, position: Int) -> Unit)? =
        null

    override fun onBind(
        holder: CheckItemDataBinding, position: Int, item: PropertyBean?,
    ) {
        holder.binding.apply {
            this.item = item
            //需要去掉监听 防止设置的时候触发上一次设置的 监听
            cbValue.setOnCheckedChangeListener(null)
            item?.apply {
                cbValue.isChecked = value.toBoolean()
            }
            cbValue.setOnCheckedChangeListener { buttonView, isChecked ->
                onCheckedChangeListener?.invoke(isChecked, item, position)
            }
        }
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int,
    ): CheckItemDataBinding {
        return CheckItemDataBinding(
            AdapterVirtualCheckBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}