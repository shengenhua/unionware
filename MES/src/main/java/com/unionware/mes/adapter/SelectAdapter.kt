package com.unionware.mes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.databinding.AdapterSelectBinding
import unionware.base.model.SelectBean

/**
 * Author: sheng
 * Date:2024/9/18
 */
class SelectAdapter<T> :
    BaseQuickAdapter<SelectBean<T>, DataBindingHolder<AdapterSelectBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterSelectBinding>,
        position: Int,
        item: SelectBean<T>?
    ) {
        holder.binding.apply {
            (item?.isCheck == true).also {
                check = it
                clSelect.isSelected = it
                tvContent.isSelected = it
            }
//            check = item?.isCheck == true
            content = item?.conntent

            /*ivCheck.setOnCheckedChangeListener { buttonView, isChecked ->

            }*/
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<AdapterSelectBinding> {
        return DataBindingHolder(
            AdapterSelectBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}