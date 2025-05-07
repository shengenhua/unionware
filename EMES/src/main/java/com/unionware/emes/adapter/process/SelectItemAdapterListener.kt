package com.unionware.emes.adapter.process

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.emes.databinding.AdapterMultiSelectItemBinding
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.CollectSelectBean

class SelectItemAdapterListener :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<CollectMultiItem, DataBindingHolder<AdapterMultiSelectItemBinding>> {
    override fun onBind(
        holder: DataBindingHolder<AdapterMultiSelectItemBinding>,
        position: Int,
        item: CollectMultiItem?
    ) {
        if (item?.value.isNullOrEmpty()) {
            item?.value = CollectSelectBean.Y.toString()
            item?.valueText = CollectSelectBean.Y.str
        }
        holder.binding.item = item
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterMultiSelectItemBinding> {
        return DataBindingHolder(
            AdapterMultiSelectItemBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}