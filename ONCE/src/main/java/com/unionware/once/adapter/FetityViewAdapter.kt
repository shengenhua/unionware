package com.unionware.once.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.databinding.AdapterOnceFentityViewBinding
import com.unionware.once.model.FentityView

class FetityViewAdapter(override var items: List<FentityView.ShowView> = emptyList()) :
    BaseQuickAdapter<FentityView.ShowView, DataBindingHolder<AdapterOnceFentityViewBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterOnceFentityViewBinding>, position: Int, item: FentityView.ShowView?
    ) {
        holder.binding.item = item
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterOnceFentityViewBinding> {
        return DataBindingHolder(
            AdapterOnceFentityViewBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}