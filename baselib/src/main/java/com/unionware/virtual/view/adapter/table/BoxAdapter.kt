package com.unionware.virtual.view.adapter.table

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterBoxBinding
import unionware.base.ext.bigDecimalToZeros

class BoxAdapter(override var items: List<String> = emptyList()) :
    BaseQuickAdapter<String, DataBindingHolder<AdapterBoxBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterBoxBinding>, position: Int, item: String?
    ) {
        holder.binding.value = try {
            item?.bigDecimalToZeros()
        } catch (e: Exception) {
            item
        }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterBoxBinding> {
        return DataBindingHolder(
            AdapterBoxBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}