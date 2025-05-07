package com.unionware.mes.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.databinding.AdapterButtonBinding
import unionware.base.model.bean.DynamicConfigBean

class JobItemBtnAdapter(override var items: List<DynamicConfigBean.JobItem> = emptyList()) :
    BaseQuickAdapter<DynamicConfigBean.JobItem, DataBindingHolder<AdapterButtonBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterButtonBinding>,
        position: Int,
        item: DynamicConfigBean.JobItem?,
    ) {
        //#1e89ef
        item?.let {
            holder.binding.acBtn.backgroundTintList = ColorStateList.valueOf(it.itemColor)
            holder.binding.buttonValue = "${it.name}\n汇报"
        }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdapterButtonBinding> {
        return DataBindingHolder(
            AdapterButtonBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}