package com.unionware.once.adapter.process

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.databinding.AdapterOnceMultiSelectItemBinding
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.CollectSelectBean

class SelectItemAdapterListener :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<CollectMultiItem, DataBindingHolder<AdapterOnceMultiSelectItemBinding>> {
    override fun onBind(
        holder: DataBindingHolder<AdapterOnceMultiSelectItemBinding>,
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
    ): DataBindingHolder<AdapterOnceMultiSelectItemBinding> {
        return DataBindingHolder(
            AdapterOnceMultiSelectItemBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}