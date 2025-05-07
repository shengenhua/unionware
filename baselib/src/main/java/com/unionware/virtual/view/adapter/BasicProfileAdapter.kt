package com.unionware.virtual.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterVirProfileBinding
import unionware.base.model.bean.BaseInfoBean

class BasicProfileAdapter : BaseQueryAdapter<BaseInfoBean, AdapterVirProfileBinding>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterVirProfileBinding>, position: Int, item: BaseInfoBean?
    ) {
        holder.binding.item = item
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterVirProfileBinding> {
        return DataBindingHolder(
            AdapterVirProfileBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}