package com.unionware.once.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.databinding.AdapterCheckBinding
import unionware.base.model.SelectBean

/**
 * Author: sheng
 * Date:2024/9/18
 */
class CheckAdapter<T> :
    BaseQuickAdapter<SelectBean<T>, DataBindingHolder<AdapterCheckBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterCheckBinding>,
        position: Int,
        item: SelectBean<T>?,
    ) {
        holder.binding.apply {
            (item?.isCheck == true).also {
                check = it
                clSelect.isSelected = it
                tvContent.isSelected = it
            }
            content = item?.conntent
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): DataBindingHolder<AdapterCheckBinding> {
        return DataBindingHolder(
            AdapterCheckBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}