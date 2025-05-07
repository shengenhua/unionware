package com.unionware.mes.process.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.process.databinding.AdptListenterSelectBinding
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.CollectSelectBean

/**
 * Author: sheng
 * Date:2024/12/4
 */
class SelectAdapterListener :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<CollectMultiItem, DataBindingHolder<AdptListenterSelectBinding>> {
    override fun onBind(
        holder: DataBindingHolder<AdptListenterSelectBinding>,
        position: Int,
        item: CollectMultiItem?,
    ) {
        if (item?.value.isNullOrEmpty()) {
            item?.value = CollectSelectBean.Y.toString()
            item?.valueText = CollectSelectBean.Y.str
        }
        holder.binding.item = item
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdptListenterSelectBinding> {
        return DataBindingHolder(
            AdptListenterSelectBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}