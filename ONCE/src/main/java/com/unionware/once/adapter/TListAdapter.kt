package com.unionware.once.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.databinding.OnceTAdapterBinding

/**
 * Author: sheng
 * Date:2025/3/12
 */
abstract class TListAdapter<T : Any> :
    BaseQuickAdapter<T, DataBindingHolder<OnceTAdapterBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<OnceTAdapterBinding>,
        position: Int,
        item: T?,
    ) {
        holder.binding.also { bind ->
            bind.name = onString(item)
        }
    }

    abstract fun onString(item: T?): String

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): DataBindingHolder<OnceTAdapterBinding> {
        return DataBindingHolder(
            OnceTAdapterBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

}